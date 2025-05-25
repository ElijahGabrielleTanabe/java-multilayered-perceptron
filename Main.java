public class Main 
{
    public static void main(String[] args) 
    {
        NeuralNetwork nn = new NeuralNetwork(2, 3, 2);

        double[] sp = {1, 0};
        double[] ts = {1, 0};

        Matrix input = new Matrix(sp);
        Matrix targets = new Matrix(ts);

        System.out.println(nn.feedForward(input));
        System.out.println(nn.train(input, targets));
    }
}
