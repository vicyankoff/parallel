#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>
#include <math.h>
#include <time.h>

/* A program to approximate PI
 * using the Monte Carlo simulation using pthreads
 * by: Viktor Jankov
 */

#define TRUE 1
#define PRINT_MULTIPLE 1000000
#define EPSILON = 1e-5;

int max_darts;
// needs a lock
int darts_in_circle;
pthread_mutex_t * darts_in_circle_lock;
// needs a lock
int total_darts_currently_thrown;
pthread_mutex_t * total_darts_currently_thrown_lock;

pthread_mutex_t * rand_lock;

int done;
int dart_is_multiple;\
pthread_mutex_t * dart_is_multiple_lock;
pthread_cond_t * dart_multiple;



void *simulate_darts (void *arg) 
{
	double x, y, c_x, c_y, dist;
	int t_id = * (int *)arg;
	free (arg);

	while (!done) 
	{

		pthread_mutex_lock(rand_lock);
		x = (double) rand() / (double) RAND_MAX;
		y = (double) rand() / (double) RAND_MAX;
		pthread_mutex_unlock(rand_lock);
		c_x = 0.5;
		c_y = 0.5;

		dist = sqrt(pow(x - c_x, 2) + pow(y - c_y, 2));
	
		if ( dist <= 0.5)
		{
			pthread_mutex_lock(darts_in_circle_lock);
			darts_in_circle++;
			pthread_mutex_unlock(darts_in_circle_lock);
		}

		pthread_mutex_lock(total_darts_currently_thrown_lock);
		total_darts_currently_thrown++;
		pthread_mutex_unlock(total_darts_currently_thrown_lock);

		if (total_darts_currently_thrown % PRINT_MULTIPLE == 0) 
		{
			dart_is_multiple = 1;
			pthread_cond_broadcast (dart_multiple);
		}

		if (total_darts_currently_thrown >= max_darts) 
		{
			done = 1;
		} 
	}
	pthread_exit (NULL);
}

void *print_pi(void *arg)
{
	while (!done) 
	{
		pthread_mutex_lock(darts_in_circle_lock);

		// Predicate P is dart is a multiple or dart is not a multiple
		while (!dart_is_multiple) 
		{
			pthread_cond_wait (dart_multiple, darts_in_circle_lock);
		}

		double result = 4 * ((double)darts_in_circle / max_darts);
		printf("Simulation: %d\n",total_darts_currently_thrown ); 
		printf("Darts in circle is: %d\n",darts_in_circle); 
		printf("Max Darts: %d\n",max_darts); 
		printf("Result: %f\n",result);
		printf("\n");
		dart_is_multiple = 0;
		pthread_mutex_unlock(darts_in_circle_lock);

	}	
	pthread_exit (NULL);
}


int main(int argc, char *argv[])
{
	int i; // looper
	time_t t; // for seeding random
	srand ((unsigned) time(&t)); // seed random

	// init the locks and cond variable
	rand_lock = (pthread_mutex_t *) malloc (sizeof (pthread_mutex_t));
	pthread_mutex_init(rand_lock, NULL);
	
	darts_in_circle_lock = (pthread_mutex_t *) malloc (sizeof (pthread_mutex_t));
	pthread_mutex_init(darts_in_circle_lock, NULL);

	total_darts_currently_thrown_lock = (pthread_mutex_t *) malloc (sizeof (pthread_mutex_t));
	pthread_mutex_init(total_darts_currently_thrown_lock, NULL);

	darts_in_circle = 0; // number of darts in circle
	total_darts_currently_thrown = 0; // total number of darts that have been thrown */

	dart_multiple = (pthread_cond_t *) malloc (sizeof (pthread_cond_t));
	pthread_cond_init (dart_multiple, NULL);
	
	done = 0;
	dart_is_multiple = 0;

	// Get the number of threads
	int number_of_threads;
	sscanf (argv[1], "%d", &number_of_threads);

	// Get the number of simulations
	sscanf (argv[2], "%d", &max_darts);

	printf("Number of threads: %d\n", number_of_threads);
	printf("Number of simulations: %d\n", max_darts);
 
	// Allocate the array of pthread pointers
	pthread_t **sim_thread = (pthread_t **) malloc (sizeof(pthread_t *) * number_of_threads);

	
	pthread_t * print_thread = (pthread_t *) malloc (sizeof(pthread_t *));
	pthread_create (print_thread,
									NULL,
									print_pi,
									NULL);

	// Allocate memory and create the simulation threads
	for (i = 0; i < number_of_threads; i++)
	{
		sim_thread[i] = (pthread_t *) malloc (sizeof(pthread_t *));
		int * sim_id = (int *) malloc (sizeof(int));
		*sim_id = i;
		if (pthread_create (sim_thread[i],
												NULL,
												simulate_darts,
												(void *) sim_id))
		{
			fprintf (stderr, "Error creating consumer thread %d.\n", i);
			exit(-1);
		}										
	}

	for (i = 0; i < number_of_threads; i++)
	{
		if (pthread_join (*sim_thread[i], NULL))
		{
			fprintf (stderr, "Error joining sim thread with id %d\n", i);
			exit(-1);
		}
	} 

	if (pthread_join (*print_thread, NULL))
	{
		fprintf (stderr, "Error joining with print_thread");
		exit(-1);
	} 
}
