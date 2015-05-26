#include "appliance.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <arpa/inet.h>

#include <libxml/xmlmemory.h>
#include <libxml/parser.h>

#define GETPROP(x, y) ((const char *) xmlGetProp(x, (xmlChar *) y))

typedef int fd;

struct appliance *parseApp(xmlNode *elem);

int sendCommandToBLuetoohDevice(char *addr, cmdset set);
int sendCommandToVirtualDevice(char *addr, cmdset set);

struct appl_list *parseApplList(const char *path) {
	struct appl_list *apps;

	xmlDocPtr doc;
	xmlNode *root = NULL;

	xmlInitParser();

	doc = xmlReadFile(path, NULL, XML_PARSE_NOBLANKS);
	if (!doc) return NULL;

	root = xmlDocGetRootElement(doc);

	// Initialize the flow.
	apps = (struct appl_list *) malloc(sizeof(struct appl_list));

	// Parse child nodes of the appliance-list.
	apps->head = parseApp(root->children);

	xmlFree(doc);
	xmlFree(root);

	return apps;
}

struct appliance *parseApp(xmlNode *elem) {
	struct appliance *app;

	// Initialize the appliance.
	app = (struct appliance *) malloc(sizeof(struct appliance));
	app->prev = app->next = NULL;

	// Parse properties of the appliance.
	app->id = atoi(GETPROP(elem, "id"));
	app->ctype = atoi(GETPROP(elem, "ctype"));
	strcpy(app->addr, GETPROP(elem, "addr"));

	switch (app->ctype) {
		case C_BLUE:
			app->runCommand = sendCommandToBLuetoohDevice;
			break;

		case C_VIRTUAL:
			app->runCommand = sendCommandToVirtualDevice;
			break;
	}

	if (elem->next) {
		app->next = parseApp(elem->next);
		app->next->prev = app;
	}

	return app;
}

void freeApplList(struct appl_list *apps) {
	struct appliance *app = NULL;

	for (app = apps->tail; app != NULL; app = app->prev) {
		free(app);
	}

	free(apps);
}

int sendCommandToBLuetoohDevice(char *addr, cmdset set) {
	// NOTE : shell script has to be like ...
	//        1. get parameters (addr, have return?)
	//        2. if, have return : do char-read-hnd
	//           if not : do not
	//        3. have to return value and this process have to get it. (exit n)
	//
	// ABLE TO HAVE THIS PROBLEM!!!
	// : connect can be failed if more than one processess try to connect with a
	//   specific ble device.

	pid_t pid;

	// TODO : Have to be changed.
	char *argv[9] = { "gatttool", "-b", addr, "--char-write-req", "-a",
			"0x0010" /*change it to non-static one*/, "-n",
			"02" /*change it to non-static one*/, NULL };

	enum cond_t cond = set.option >> 24;
	uint value = (set.option << 8) >> 8;
	int curr = -1;

	if ((pid = fork())) {
		wait(&curr);
	} else {
		if (execv("/usr/bin/gatttool", argv) == -1) {
			printf("test : Failed to execute gatttool\n");
			exit(1);
		}
	}

	// TODO : Consider about the codes below into the shell.
	if (set.type == O_NOWAIT) {
		int result = 0;

		// TODO : fork again.

		if (curr == value) result += (1 << 0);
		if (curr < value) result += (1 << 1);
		if (curr > value) result += (1 << 2);

		return (result & cond);
	} else if (set.type == O_WAIT) {
		int result = 0;

		do {
			result = curr= 0;
			
			// fork again.

			if (curr == value) result += (1 << 0);
			if (curr < value) result += (1 << 1);
			if (curr > value) result += (1 << 2);
		} while (!(result & cond));
	}

	return 1;
}

int sendCommandToVirtualDevice(char *addr, cmdset set) {
	enum cond_t cond = set.option >> 24;
	uint value = (set.option << 8) >> 8;

	switch (atoi(addr)) {
		case V_TIMER:  // Available only for 'O_NOWAIT' and 'O_WAIT'.
			if (set.command == 1) {
				time_t t = time(NULL);
				struct tm *temp = localtime(&t);
				int wday_mask = value >> 17;

				uint goal = 0, curr = 0;

				if (!(wday_mask & (1 << (6 - temp->tm_wday)))) return 0;

				goal += ((value << 15) >> 27) * 3600;
				goal += ((value << 20) >> 26) * 60;
				goal += ((value << 26) >> 26);

				curr += temp->tm_hour * 3600;
				curr += temp->tm_min * 60;
				curr += temp->tm_sec;

				if (set.type == O_NOWAIT) {
					int result = 0;

					if (curr == goal) result += (1 << 0);
					if (curr < goal) result += (1 << 1);
					if (curr > goal) result += (1 << 2);

					return (result & cond);
				} else if (set.type == O_WAIT) {
					// Available only for '=='.

					if (curr > goal) return 0;

					sleep((goal - curr) * 1000);
					return 1;
				}
			}
			break;

		case V_COUNTER:  // Available only for 'O_NOWAIT'.
			if (set.command == 1) {
				static int count = 0;
				int result = 0;

				++count;

				if (count == value) result += (1 << 0);
				if (count < value) result += (1 << 1);
				if (count > value) result += (1 << 2);

				if (!(result & cond)) count = -1;
				return (result & cond);
			}
			break;
	}

	return 0;
}
