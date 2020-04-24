using System;
using System.Diagnostics;
using System.Threading.Tasks;

namespace Lab_9_csharp
{
    public class MatrixMulty
    {
        private float[] mat1 = new float[SIZE * SIZE];
        private float[] mat2 = new float[SIZE * SIZE];
        private float[] mat_ans = new float[SIZE * SIZE];

        private const int SIZE = 8;
        private const int processNum = 2;

        private Random random = new Random();

        public void setRandMat1()
        {
            for (int i = 0; i < SIZE; ++i)
                for (int j = 0; j < SIZE; ++j)
                    mat1[i * SIZE + j] = (((float)random.NextDouble()) - 0.5f) * 2.0f;
        }


        public void setRandMat2()
        {
            for (int i = 0; i < SIZE; ++i)
                for (int j = 0; j < SIZE; ++j)
                    mat2[i * SIZE + j] = (((float)random.NextDouble()) - 0.5f) * 2.0f;
        }

        public float tapeCircuit()
        {
            Stopwatch watcher = Stopwatch.StartNew();

            Parallel.For(0, SIZE, new ParallelOptions { MaxDegreeOfParallelism = processNum }, i =>
            {
                for (int j = 0; j < SIZE; j++)
                {
                    mat_ans[i * SIZE + j] = 0.0f;
                    for (int k = 0; k < SIZE; ++k)
                        mat_ans[i * SIZE + j] += mat1[i * SIZE + k] * mat2[k * SIZE + j];
                }
            });

            watcher.Stop();

            return (float)watcher.ElapsedMilliseconds / 1000.0f;
        }
    }



    class Program
    {
        static void Main(string[] args)
        {
            MatrixMulty mat = new MatrixMulty();
            mat.setRandMat1();
            mat.setRandMat2();

            Console.WriteLine(mat.tapeCircuit());
        }
    }
}
