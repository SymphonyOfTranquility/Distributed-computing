//
// Created by art on 4/23/20.
//
#include <stdlib.h>
#include <stdio.h>

# include "TMatrix.h"

void TMatrix_init(Mat *mat, unsigned int row_size, unsigned int col_size)
{
    mat->at = &TMatrix_at;
    mat->del = &TMatrix_del;
    mat->set = &TMatrix_set;
    mat->random_fill = &TMatrix_random_fill;
    mat->zero_fill = &TMatrix_zero_fill;
    mat->resize = &TMatrix_resize;
    mat->output = &TMatrix_output;
    mat->id = NULL;
    mat->resize(mat, row_size, col_size);
}

void TMatrix_resize(Mat* mat, unsigned int new_row_size, unsigned int new_col_size)
{
    mat->del(mat);
    mat->row_size = new_row_size;
    mat->col_size = new_col_size;
    mat->id = malloc(mat->row_size * mat->col_size * sizeof(float));
}

float TMatrix_at(Mat *mat, unsigned int i, unsigned int j)
{
    if (i < mat->row_size && j < mat->col_size)
        return *(mat->id + i * mat->col_size + j);
    else
        return 0.f;
}

void TMatrix_set(Mat *mat, unsigned int i, unsigned int j, float value)
{
    if (i < mat->row_size && j < mat->col_size)
        *(mat->id + i * mat->col_size + j) = value;
}

void TMatrix_del(Mat *mat)
{
    if (mat->id != NULL)
        free(mat->id);
}

void TMatrix_random_fill(Mat *mat)
{
    for (int i = 0;i < mat->row_size; ++i)
        for (int j = 0;j < mat->col_size; ++j)
            mat->set(mat, i, j, (float) ((float) rand() - (float) RAND_MAX / 2.f) / (float) RAND_MAX);
}


void TMatrix_zero_fill(Mat *mat)
{
    for (int i = 0;i < mat->row_size; ++i)
        for (int j = 0;j < mat->col_size; ++j)
            mat->set(mat, i, j, 0.f);
}


void TMatrix_output(Mat* mat)
{
    printf("%dx%d\n", mat->row_size, mat->col_size);
    for (int i = 0;i < mat->row_size; ++i)
    {
        for (int j = 0; j < mat->col_size; ++j)
            printf("%f ", mat->at(mat, i, j));
        printf("\n");
    }
}