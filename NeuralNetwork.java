public class NeuralNetwork
{
    private Matrix weightsIH;
    private Matrix weightsHO;

    private Matrix biasH;
    private Matrix biasO;

    private double learningRate;

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

        this.learningRate = 0.1;
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

    public Matrix train(Matrix inputs, Matrix targets)
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
        Matrix outputs = Matrix.matrixMultiply(this.weightsHO, hidden);
        //Apply Bias
        outputs.add(this.biasO);
        //Apply Activation Function (return a double between 0 - 1)
        outputs.map(x -> sigmoid(x));

        Matrix outputErrors = Matrix.subtract(targets, outputs);

        //Gradient = s(x) * (1 - s(x))
        //Calculate output-gradient
        Matrix outputGradient = Matrix.map(outputs, x-> dSigmoid(x));
        outputGradient.multiply(outputErrors); //Hadamard product
        outputGradient.scalerMultiply(this.learningRate);

        //Calculate delta weights for weightsHO
        Matrix deltaWeightsHO = Matrix.matrixMultiply(outputGradient, Matrix.transpose(hidden));

        //Apply changes to weightsHO
        this.weightsHO.add(deltaWeightsHO);
        //Apply changes to biasO
        this.biasO.add(outputGradient);

        //Calculate hidden layer errors
        Matrix tWeightsHO = Matrix.transpose(this.weightsHO); 
        Matrix hiddenErrors = Matrix.matrixMultiply(tWeightsHO, outputErrors);

        //Calculate hidden-gradient
        Matrix hiddenGradient = Matrix.map(hidden, x -> dSigmoid(x));
        hiddenGradient.multiply(hiddenErrors); //Hadamard product
        hiddenGradient.scalerMultiply(this.learningRate);

        //Calculate delta weights for weightIH
        Matrix deltaWeightsIH = Matrix.matrixMultiply(hiddenGradient, Matrix.transpose(inputs));

        //Apply changes to weightsIH
        this.weightsIH.add(deltaWeightsIH);
        //Apply changes to biasH
        this.biasH.add(hiddenGradient);

        return null;
    }

    private double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(x));
    }

    private double dSigmoid(double x)
    {
        return x * (1 - x);
    }
}