#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <errno.h>

#include "scheduler.h"

void scheduleEvents(struct scheduler *scheduler, int mode) {
	const char *path = "./user/flows/";
	DIR *dir;
	struct dirent *eBuf;

	struct event *curr = NULL;

	if (mode == REDO) {
		path = "./user/temp/";

		if (scheduler->head)
			curr = scheduler->tail;
		else
			mode = INIT;
	}

	if (!(dir = opendir(path))) return;

	while ((eBuf = readdir(dir))) {
		char full[BUFSIZ];

		if (eBuf->d_name[0] == '.') continue;

		sprintf(full, "%s%s", path, eBuf->d_name);

		if (curr) {
			curr->next = (struct event *) malloc(sizeof(struct event));
			curr->next->prev = curr;
			curr = curr->next;
		} else {
			scheduler->head = (struct event *) malloc(sizeof(struct event));
			curr = scheduler->head;
			curr->prev = NULL;
		}

		curr->pid = 0;
		curr->next = NULL;
		curr->flow = parseFlow(full);

		if (mode == REDO) {
			char origin[BUFSIZ];
			char *ptr = strpbrk(eBuf->d_name, ".");
			int id;

			sprintf(origin, "./user/flows/%s", eBuf->d_name);

			struct event *old;

			*ptr = '\0';
			id = atoi(eBuf->d_name);
			*ptr = '.';

			for (old = scheduler->head; old->next != NULL; old = old->next) {
				if (old->flow->id == id) {
					if (old->prev) old->prev->next = old->next;
					if (old->next) old->next->prev = old->prev;

					freeFlow(old->flow);
					free(old);

					remove(origin);
					break;
				}
			}

			rename(full, origin);
		}
	}

	scheduler->tail = curr;
}
