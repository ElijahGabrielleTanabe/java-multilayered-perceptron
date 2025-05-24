public class NeuralNetwork
{
    private Matrix weightsIH;
    private Matrix weightsHO;

    private Matrix biasH;
    private Matrix biasO;

    public NeuralNetwork(int inputNodes, int hiddenNodes, int outputNodes)
    {
        this.weightsIH = new Matrix(hiddenNodes, inputNodes);
        this.weightsHO = new Matrix(outputNodes, hiddenNodes);

        this.weightsIH.randomize();
        this.weightsHO.randomize();

        this.biasH = new Matrix(hiddenNodes, 1);
        this.biasO = new Matrix(outputNodes, 1);

        this.biasH.randomize();
        this.biasO.randomize();
    }

    public Matrix feedForward(Matrix inputs)
    {
        //I -> H
        //Weighted Sum
        Matrix hidden = Matrix.matrixMultiply(this.weightsIH, inputs);
        //Apply Bias
        hidden.add(this.biasH);
        //Apply Activation Function (returns a double between 0 - 1)
        hidden.map(x -> sigmoid(x));

        //H -> O
        //Weight Sum
        Matrix output = Matrix.matrixMultiply(this.weightsHO, hidden);
        //Apply Bias
        output.add(this.biasO);
        //Apply Activation Function (return a double between 0 - 1)
        output.map(x -> sigmoid(x));

        return output;
    }

    public void train(Matrix inputs, Matrix targets)
    {
        Matrix outputs = this.feedForward(inputs);

        Matrix error = Matrix.substract(targets, outputs);
    }

    private double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(x));
    }
}