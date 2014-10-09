#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

	int second_person_total_number_of_times;
	int * second_person_avail_times;

	int third_person_total_number_of_times;
	int * third_person_avail_times;

// if the first person's time matches with any of the second person times
// check the times of the third person
void *compare_times(void * arg) {
	int first_person_time = * (int *) arg;
	free(arg);
	int i, j;
	for(i = 0; i < second_person_total_number_of_times; i++) {
		if( second_person_avail_times[i] == first_person_time) {
			for(j = 0; j < third_person_total_number_of_times; j++) {
				if( first_person_time == third_person_avail_times[j]) {
					printf("%d is a common meeting time.\n", first_person_time);
				}
			}
		}
	}
	return(NULL);
}

// fill out each person's available times based on the first number in the line
void fill_array(FILE *myFile, int **person_avail_times, int *person_total_times) {
	fscanf(myFile, "%d", person_total_times);
	*person_avail_times = (int *) malloc(sizeof(int) * *person_total_times);
	int i;
	for(i = 0; i < *person_total_times; i++) {
		fscanf(myFile, "%d", &(*person_avail_times)[i]);
	}
}

int main(int argc, char *argv[])
{

	FILE *myFile = fopen(argv[1], "r");
	int first_person_total_number_of_times, i;
	int * first_person_avail_times;

	// Fill out the arrays with the three people's available times
	fill_array(myFile, &first_person_avail_times, &first_person_total_number_of_times);
	fill_array(myFile, &second_person_avail_times, &second_person_total_number_of_times);
	fill_array(myFile, &third_person_avail_times, &third_person_total_number_of_times);

	// allocate memory for the threads of first person's times
	pthread_t *tid[first_person_total_number_of_times];
	for(i = 0; i < first_person_total_number_of_times; i++) {
		tid[i] = (pthread_t *) malloc(sizeof(pthread_t));
	}

	// Start the threads to calculate common times
	for(i = 0; i < first_person_total_number_of_times; i++) {
		int *first_person_time = (int *) malloc(sizeof(int));
		*first_person_time = first_person_avail_times[i];
		if( pthread_create( tid[i], NULL,
					compare_times,
					(void *) first_person_time)) {

			fprintf (stderr, "Error creating thread %d\n", i);
		}
	}

	for(i = 0; i < first_person_total_number_of_times; i++) {
		if(pthread_join(*tid[i], NULL)) {
			fprintf(stderr, "Error joining with thread %d", i);
		}
	}

	exit(0);
}
