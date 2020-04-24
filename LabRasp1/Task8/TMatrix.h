//
// Created by art on 4/23/20.
//

#ifndef TASK8_TMATRIX_H
#define TASK8_TMATRIX_H

#endif //TASK8_TMATRIX_H

typedef struct TMatrix Mat;

struct TMatrix
{
    unsigned int row_size;
    unsigned int col_size;
    float *id;
    void (*init)(Mat*, unsigned int, unsigned int);
    float (*at)(Mat*, unsigned int, unsigned int);
    void (*set)(Mat*, unsigned int, unsigned int, float);
    void (*del)(Mat*);
    void (*random_fill)(Mat*);
    void (*zero_fill)(Mat*);
    void (*resize)(Mat*, unsigned int, unsigned int);
    void (*output)(Mat*);
};

void TMatrix_init(Mat *mat, unsigned int row_size, unsigned int col_size);
float TMatrix_at(Mat *mat, unsigned int i, unsigned int j);
void TMatrix_set(Mat *mat, unsigned int i, unsigned int j, float value);
void TMatrix_del(Mat *del);
void TMatrix_random_fill(Mat *mat);
void TMatrix_zero_fill(Mat *mat);
void TMatrix_resize(Mat* mat, unsigned int new_row_size, unsigned int new_col_size);
void TMatrix_output(Mat* mat);