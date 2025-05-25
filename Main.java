import java.util.Random;

public class Main 
{
    public static void main(String[] args) 
    {
        NeuralNetwork nn = new NeuralNetwork(2, 2, 1);

        Matrix[] inputs = {
            new Matrix(new double[]{0, 1}),
            new Matrix(new double[]{1, 0}),
            new Matrix(new double[]{0, 0}),
            new Matrix(new double[]{1, 1})
        };

        Matrix[] targets = {
            new Matrix(new double[]{1}),
            new Matrix(new double[]{1}),
            new Matrix(new double[]{0}),
            new Matrix(new double[]{0})
        };

        for (int j = 0; j < 500000; j++)
        {
            Random rand = new Random();
            int i = rand.nextInt(4);

            nn.train(inputs[i], targets[i]);
        }

        System.out.println(nn.feedForward(new Matrix(new double[]{0, 1})));
        System.out.println(nn.feedForward(new Matrix(new double[]{1, 0})));
        System.out.println(nn.feedForward(new Matrix(new double[]{0, 0})));
        System.out.println(nn.feedForward(new Matrix(new double[]{1, 1})));
    }
}
