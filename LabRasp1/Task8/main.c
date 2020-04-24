# include <stdlib.h>
# include <stdio.h>
# include <time.h>
# include <string.h>

# include "mpi.h"
#include "TMatrix.h"


// DEFINES FOR ALGOS
#ifdef SEQUENTIAL
    int ALGO_MODE = 1;
#elif TAPE_CIRCUIT
    int ALGO_MODE = 2;
#elif FOX
    int ALGO_MODE = 3;
#elif KENNON
    int ALGO_MODE = 4;
#else
    int ALGO_MODE = 2;
#endif

const int SIZE = 4;

int main();
void timestamp();
void multiply_matrix(float *mat1, float *mat2, float** mat_ans, int process_id, int process_num);
float* sequential(float *mat1, float *mat2, int size);
float* tape_script(float *mat1, float *mat2, int process_id, int process_number);
void kennon_algo(float *mat1, float *mat2, float **mat_ans, int process_id, int process_num);
void fox_algo(float *mat1, float *mat2, float **mat_ans, int process_id, int process_num);

int min(int a, int b);
float *random_fill(int size, float is_zero);
void send_to_other_processes(float *mat1, float *mat2, int number_of_processes);
void recv_from_main_process(float **mat1, float **mat2, int number_of_processes);
void is_done(int process_id, int process_number, float* ans_process, float **overall_ans);

int main()
{
    if (ALGO_MODE == -1)
    {
        printf("Algo is not predefined. Choose one while compilation. \n(They all given in top of main file)");
        printf("Command you need to add while compile \"-D define_name\"");
        return 0;
    }
    int process_id; int ret_err; int number_of_processes;
    double wtime;

    ret_err = MPI_Init (NULL, NULL);
    if (ret_err != 0)
    {
        printf ( "  MPI_Init returned nonzero IERR.\n" );
        exit ( 1 );
    }
    ret_err = MPI_Comm_size (MPI_COMM_WORLD, &number_of_processes );
    ret_err = MPI_Comm_rank (MPI_COMM_WORLD, &process_id );

    float *mat1 = NULL, *mat2 = NULL, *mat_ans = NULL;

    if (process_id == 0)
    {
        mat1 = random_fill(SIZE, 1.f); mat2 = random_fill(SIZE, 1.f);
        send_to_other_processes(mat1, mat2, number_of_processes);
        wtime = MPI_Wtime();
        printf ("The number of processes is %d.\n", number_of_processes );
    }
    else
    {
        recv_from_main_process(&mat1, &mat2, number_of_processes);
    }

    multiply_matrix(mat1, mat2, &mat_ans, process_id, number_of_processes);

    if (process_id == 0)
    {
        wtime = MPI_Wtime() - wtime;
        printf ("Elapsed wall clock time = %f seconds.\n", wtime );
    }

    ret_err = MPI_Finalize();

    if (process_id == 0)
    {
        /*
        for (int i = 0;i < SIZE; ++i)
        {
            for (int j = 0; j < SIZE; ++j)
                printf("%f ", *(mat1 + i * SIZE + j));
            printf("\n");
        }
        printf("\n");
        for (int i = 0;i < SIZE; ++i)
        {
            for (int j = 0; j < SIZE; ++j)
                printf("%f ", *(mat2 + i * SIZE + j));
            printf("\n");
        }

        printf("\n");
        for (int i = 0;i < SIZE; ++i)
        {
            for (int j = 0; j < SIZE; ++j)
                printf("%f ", *(mat_ans + i * SIZE + j));
            printf("\n");
        }
*/
        printf("Done\n");
        timestamp();
    }
    if (mat1 != NULL) free(mat1);
    if (mat2 != NULL) free(mat2);
    if (mat_ans != NULL) free(mat_ans);
    return 0;
}

void multiply_matrix(float *mat1, float *mat2, float** mat_ans, int process_id, int process_num)
{
    if (ALGO_MODE == 1) //SEQUENTIAL
    {
        if (process_id == 0)
            (*mat_ans) = sequential(mat1, mat2, SIZE);
    }
    else if (ALGO_MODE == 2) // TAPE CIRCUIT
    {
        float *ans = tape_script(mat1, mat2, process_id, process_num);
        if (process_id == 0)
            (*mat_ans) = random_fill(SIZE, 0.f);
        is_done(process_id, process_num, ans, mat_ans);
    }
    else if (ALGO_MODE == 3)    // FOX
    {
        if (process_id == 0)
            (*mat_ans) = random_fill(SIZE, 0.f);
        if (process_num != 4)
            printf("Have to be exactly 4 processes\n");
        else
            fox_algo(mat1, mat2, mat_ans, process_id, process_num);
    }
    else if (ALGO_MODE == 4) // KENNON
    {
        if (process_id == 0)
            (*mat_ans) = random_fill(SIZE, 0.f);
        if (process_num != 4)
            printf("Have to be exactly 4 processes\n");
        else
            kennon_algo(mat1, mat2, mat_ans, process_id, process_num);
    }
    else
        printf("No func\n");
}

float *random_fill(int size, float is_zero)
{
    float *mat = malloc(size * size * sizeof(float));
    for (int i = 0;i < size; ++i)
        for (int j = 0;j < size; ++j)
            *(mat + i*size + j) = is_zero * (float) ((float) rand() - (float) RAND_MAX / 2.f) / (float) RAND_MAX;

    return mat;
}

void send_to_other_processes(float *mat1, float *mat2, int number_of_processes)
{
    int tag = 1;
    if (ALGO_MODE == 1) //SEQUENTIAL
        return;
    if (ALGO_MODE == 2) // TAPE CIRCUIT
    {
        int step = -min(-SIZE/number_of_processes, -1);
        for (int pid = 1; pid < number_of_processes; ++pid)
        {
            MPI_Send(&mat1[pid*step*SIZE], step*SIZE, MPI_FLOAT, pid, tag, MPI_COMM_WORLD);
            MPI_Send(&mat2[0], SIZE*SIZE, MPI_FLOAT, pid, tag, MPI_COMM_WORLD);
        }
    }
    else if (ALGO_MODE == 3 || ALGO_MODE == 4)    //FOX or KENNON
        return;
    else
        printf("No func\n");
}

void recv_from_main_process(float **mat1, float **mat2, int number_of_processes)
{
    if (ALGO_MODE == 1) //SEQUENTIAL
        return;
    if (ALGO_MODE == 2) // TAPE CIRCUIT
    {
        int step = -min(-SIZE/number_of_processes, -1);
        int tag = 1;
        (*mat1) = malloc(step*SIZE*sizeof(float));
        (*mat2) = malloc(SIZE*SIZE*sizeof(float));
        MPI_Status status;
        MPI_Recv(&((*mat1)[0]), step*SIZE, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&((*mat2)[0]), SIZE*SIZE, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
    }
}

void is_done(int process_id, int process_number, float* ans_process, float **overall_ans)
{
    int get = -1, tag = 2;
    int step = -min(-SIZE/process_number, -1);
    if (process_id == 0)
    {
        MPI_Status status;
        for (int i = 0;i < SIZE*step; ++i)
            *((*overall_ans) + i) = *(ans_process + i);

        for (int pid = 1; pid < process_number; ++pid)
        {
            MPI_Recv(ans_process, step * SIZE, MPI_FLOAT, pid, tag, MPI_COMM_WORLD, &status);
            for (int i = 0;i < SIZE*step; ++i)
                *((*overall_ans) + i + pid*SIZE*step) = *(ans_process + i);
        }
    } else{
        MPI_Send(ans_process, step*SIZE, MPI_FLOAT, 0, tag, MPI_COMM_WORLD);
    }
}

int min(int a, int b)
{
    if (a < b) return a;
    else return b;
}

float* sequential(float *mat1, float *mat2, int size)
{
    float* ans = random_fill(size, 0.f);
    for (int i = 0;i < size; ++i)
        for (int j = 0;j < size; ++j)
            for (int k = 0;k < size; ++k)
                *(ans + i*size + j) += *(mat1 + i*size + k)* *(mat2 + k*size + j);
    return ans;
}

float* tape_script(float *mat1, float *mat2, int process_id, int process_number)
{
    int step = -min(-SIZE/process_number, -1);
    float *ans = malloc(sizeof(float)*SIZE*step);
    for (int i = 0; i < step; ++i)
        for (int j = 0; j < SIZE; ++j)
        {
            *(ans + i * SIZE + j) = 0;
            for (int k = 0; k < SIZE; ++k)
                *(ans + i * SIZE + j) += *(mat1 + i * SIZE + k) * *(mat2 + k * SIZE + j);
        }

    return ans;
}

void kennon_algo(float *mat1, float *mat2, float **mat_ans, int process_id, int process_num)
{
    const int parts = 2;
    int smaller_size = SIZE/parts;
    float *small_mat1 = random_fill(smaller_size, 0.f);
    float *small_mat2 = random_fill(smaller_size, 0.f);
    float *small_mat_ans = random_fill(smaller_size, 0.f);
    int tag = 1;
    if (process_id == 0)
    {
        for (int i = 0; i < smaller_size; ++i)
            memcpy(&small_mat1[i*smaller_size], &mat1[i*SIZE], smaller_size*sizeof(float));

        for (int i = 0; i < smaller_size; ++i)
            memcpy(&small_mat2[i*smaller_size], &mat2[i*SIZE], smaller_size*sizeof(float));

        for (int i = 1; i < process_num; ++i)
        {
            int row = i/parts;
            int col = i%parts;
            for (int j = 0;j < smaller_size; ++j)
                MPI_Send(&mat1[j*SIZE + row*smaller_size*SIZE + col*smaller_size],
                        smaller_size, MPI_FLOAT, i, tag, MPI_COMM_WORLD);
            for (int j = 0;j < smaller_size; ++j)
                MPI_Send(&mat2[j*SIZE + row*smaller_size*SIZE + col*smaller_size],
                        smaller_size, MPI_FLOAT, i, tag, MPI_COMM_WORLD);
        }
    }
    MPI_Status status;
    if (process_id != 0)
    {
        for (int i = 0;i < smaller_size; ++i)
            MPI_Recv(&small_mat1[i*smaller_size], smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
        for (int i = 0;i < smaller_size; ++i)
            MPI_Recv(&small_mat2[i*smaller_size], smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
    }

    float *temp_mat = random_fill(smaller_size, 0.f);
    for (int it = 0;it < parts; ++it)
    {
        float *temp = sequential(small_mat1, small_mat2, smaller_size);
        for (int i = 0;i < smaller_size; ++i)
            for (int j = 0; j < smaller_size; ++j)
                *(small_mat_ans + i * smaller_size + j) += *(temp + i * smaller_size + j);
        int row_n = -1, col_n = -1;
        if (process_id == 0)
        {
            row_n = 1; col_n = 2;
        }
        else if (process_id == 1)
        {
            row_n = 0; col_n = 3;
        }
        if (process_id == 2)
        {
            row_n = 3; col_n = 0;
        }
        else if (process_id == 3)
        {
            row_n = 2; col_n = 1;
        }

        if (process_id % 2 == 0)
        {
            MPI_Send(&small_mat1[0], smaller_size * smaller_size, MPI_FLOAT, row_n, tag, MPI_COMM_WORLD);
            MPI_Recv(&small_mat1[0], smaller_size * smaller_size, MPI_FLOAT, row_n, tag, MPI_COMM_WORLD, &status);
        } else
        {
            memcpy(&temp_mat[0], &small_mat1[0], smaller_size * smaller_size * sizeof(float));
            MPI_Recv(&small_mat1[0], smaller_size * smaller_size, MPI_FLOAT, row_n, tag, MPI_COMM_WORLD, &status);
            MPI_Send(&temp_mat[0], smaller_size * smaller_size, MPI_FLOAT, row_n, tag, MPI_COMM_WORLD);
        }

        if (process_id / 2 == 0)
        {
            MPI_Send(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD);
            MPI_Recv(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD, &status);
        } else
        {
            memcpy(&temp_mat[0], &small_mat2[0], smaller_size * smaller_size * sizeof(float));
            MPI_Recv(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD, &status);
            MPI_Send(&temp_mat[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD);
        }
    }

    tag = 2;
    if (process_id != 0)
    {
        MPI_Send(&small_mat_ans[0], smaller_size * smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD);
    }
    else {
        for (int i = 0;i < smaller_size; ++i)
            for (int j = 0;j < smaller_size; ++j)
                *((*mat_ans) + i*SIZE + j) = *(small_mat_ans + i*smaller_size + j);
        for (int pid = 1; pid < process_num; ++pid)
        {
            int row = pid/parts;
            int col = pid%parts;
            MPI_Recv(&temp_mat[0], smaller_size*smaller_size, MPI_FLOAT, pid, tag, MPI_COMM_WORLD, &status);
            for (int i = 0;i < smaller_size; ++i)
                for (int j = 0;j < smaller_size; ++j)
                    *((*mat_ans) + (i + smaller_size*row)*SIZE + (j + smaller_size*col)) = *(temp_mat + i*smaller_size + j);
        }
    }
    free(temp_mat);
    free(small_mat1);
    free(small_mat2);
    free(small_mat_ans);
}

void fox_algo(float *mat1, float *mat2, float **mat_ans, int process_id, int process_num)
{
    const int parts = 2;
    int smaller_size = SIZE/parts;
    float *small_mat1 = random_fill(smaller_size, 0.f);
    float *small_mat2 = random_fill(smaller_size, 0.f);
    float *small_mat_ans = random_fill(smaller_size, 0.f);
    float *native_mat1 = random_fill(smaller_size, 0.f);
    int tag = 1;
    if (process_id == 0)
    {
        for (int i = 0; i < smaller_size; ++i)
            memcpy(&small_mat1[i*smaller_size], &mat1[i*SIZE], smaller_size*sizeof(float));

        for (int i = 0; i < smaller_size; ++i)
            memcpy(&small_mat2[i*smaller_size], &mat2[i*SIZE], smaller_size*sizeof(float));

        for (int i = 1; i < process_num; ++i)
        {
            int row = i/parts;
            int col = i%parts;
            for (int j = 0;j < smaller_size; ++j)
                MPI_Send(&mat1[j*SIZE + row*smaller_size*SIZE + col*smaller_size],
                         smaller_size, MPI_FLOAT, i, tag, MPI_COMM_WORLD);
            for (int j = 0;j < smaller_size; ++j)
                MPI_Send(&mat2[j*SIZE + row*smaller_size*SIZE + col*smaller_size],
                         smaller_size, MPI_FLOAT, i, tag, MPI_COMM_WORLD);
        }
    }
    MPI_Status status;
    if (process_id != 0)
    {
        for (int i = 0;i < smaller_size; ++i)
            MPI_Recv(&small_mat1[i*smaller_size], smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
        for (int i = 0;i < smaller_size; ++i)
            MPI_Recv(&small_mat2[i*smaller_size], smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD, &status);
    }

    memcpy(&native_mat1[0], &small_mat1[0], smaller_size * smaller_size * sizeof(float));
    float *temp_mat = random_fill(smaller_size, 0.f);
    for (int it = 0;it < parts; ++it)
    {
        memcpy(&small_mat1[0],&native_mat1[0], smaller_size * smaller_size * sizeof(float));

        int next_col = (process_id%parts + it)%parts;
        int next_process_row = next_col + parts*(process_id/parts);
        if (next_process_row != process_id)
        {
            MPI_Send(&small_mat1[0], smaller_size * smaller_size, MPI_FLOAT, next_process_row, tag, MPI_COMM_WORLD);
            MPI_Recv(&small_mat1[0], smaller_size*smaller_size, MPI_FLOAT, next_process_row, tag, MPI_COMM_WORLD, &status);
        }
        float *temp = sequential(small_mat1, small_mat2, smaller_size);
        for (int i = 0;i < smaller_size; ++i)
            for (int j = 0; j < smaller_size; ++j)
                *(small_mat_ans + i * smaller_size + j) += *(temp + i * smaller_size + j);

        int col_n = -1;
        if (process_id == 0) col_n = 2;
        else if (process_id == 1) col_n = 3;
        if (process_id == 2) col_n = 0;
        else if (process_id == 3) col_n = 1;

        if (process_id / 2 == 0)
        {
            MPI_Send(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD);
            MPI_Recv(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD, &status);
        } else
        {
            memcpy(&temp_mat[0], &small_mat2[0], smaller_size * smaller_size * sizeof(float));
            MPI_Recv(&small_mat2[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD, &status);
            MPI_Send(&temp_mat[0], smaller_size * smaller_size, MPI_FLOAT, col_n, tag, MPI_COMM_WORLD);
        }
    }

    tag = 2;
    if (process_id != 0)
    {
        MPI_Send(&small_mat_ans[0], smaller_size * smaller_size, MPI_FLOAT, 0, tag, MPI_COMM_WORLD);
    }
    else {
        for (int i = 0;i < smaller_size; ++i)
            for (int j = 0;j < smaller_size; ++j)
                *((*mat_ans) + i*SIZE + j) = *(small_mat_ans + i*smaller_size + j);
        for (int pid = 1; pid < process_num; ++pid)
        {
            int row = pid/parts;
            int col = pid%parts;
            MPI_Recv(&temp_mat[0], smaller_size*smaller_size, MPI_FLOAT, pid, tag, MPI_COMM_WORLD, &status);
            for (int i = 0;i < smaller_size; ++i)
                for (int j = 0;j < smaller_size; ++j)
                    *((*mat_ans) + (i + smaller_size*row)*SIZE + (j + smaller_size*col)) = *(temp_mat + i*smaller_size + j);
        }
    }
    free(temp_mat);
    free(small_mat1);
    free(small_mat2);
    free(small_mat_ans);
}


void timestamp()
{
# define TIME_SIZE 40

    static char time_buffer[TIME_SIZE];
    const struct tm *tm;
    time_t now;

    now = time ( NULL );
    tm = localtime ( &now );

    strftime ( time_buffer, TIME_SIZE, "%d %B %Y %I:%M:%S %p", tm );

    printf ( "%s\n", time_buffer );

# undef TIME_SIZE
}