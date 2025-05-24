public class Main 
{
    public static void main(String[] args) 
    {
        NeuralNetwork nn = new NeuralNetwork(2, 2, 1);

        double[] sp = {200, 150};

        Matrix input = new Matrix(sp);

        System.out.println(nn.feedForward(input));
    }
}
