#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

	int second_array_size;
	int third_array_size;
	int * second;
	int * third;

void *compare_times(void * arg) {
	int compare_value = * (int *) arg;
	free(arg);
	int i;
	int j;
	for(i = 0; i < second_array_size; i++) {
		if( second[i] == compare_value) {
			for(j = 0; j < third_array_size; j++) {
				if( compare_value == third[j]) {
					printf("%d is a common meeting time.\n", compare_value);
				}
			}
		}
	}
	return(NULL);
}

void fill_array(FILE *myFile, int **array, int *array_size) {
	int value;
	fscanf(myFile, "%d", &value);
	*array_size = value;
	*array = (int *) malloc(sizeof(int) * value);
	int i;
	for(i = 0; i < *array_size; i++) {
		fscanf(myFile, "%d", &(*array)[i]);
	}
}

int main(int argc, char *argv[])
{

	FILE *myFile = fopen(argv[1], "r");
	int * first;
	int first_array_size;

	fill_array(myFile, &first, &first_array_size);
	fill_array(myFile, &second, &second_array_size);
	fill_array(myFile, &third, &third_array_size);

	int i;
	pthread_t *tid[first_array_size];
	for(i = 0; i < first_array_size; i++) {
		tid[i] = (pthread_t *) malloc(sizeof(pthread_t));
	}

	for(i = 0; i < first_array_size; i++) {
		int *the_parm = (int *) malloc(sizeof(int));
		*the_parm = first[i];
		if( pthread_create( tid[i], NULL,
					compare_times,
					(void *) the_parm)) {

			fprintf (stderr, "Error creating thread %d\n", i);
		}
	}

	void **thread_ret_val = (void *) malloc (sizeof (void*));
	for(i = 0; i < first_array_size; i++) {
		if(pthread_join(*tid[i], thread_ret_val)) {
			fprintf(stderr, "Error joining with thread %d", i);
		}
	}

	exit(0);
}
