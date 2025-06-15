package com.github.elijahgabrielletanabe.Model;

public class NeuralNetwork
{
    private Matrix hiddenOut;
    private Matrix outputOut;

    private Matrix weightsIH;
    private Matrix weightsHO;

    private Matrix biasH;
    private Matrix biasO;

    private Matrix velocityIH;
    private Matrix velocityHO;

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

        this.velocityIH = new Matrix(hiddenNodes, inputNodes);
        this.velocityHO = new Matrix(outputNodes, hiddenNodes);

        this.hiddenOut = new Matrix(new double[hiddenNodes]);
        this.outputOut = new Matrix(new double[outputNodes]);

        this.learningRate = 0.01;
    }

    public Matrix feedForwardOnNode(Matrix inputs, int node)
    {
        double[] weights = weightsIH.getMatrix()[node];
        Matrix weightMatrix = new Matrix(new double[][]{weights});

        double[] bias = biasH.getMatrix()[node];
        Matrix biasMatrix = new Matrix(bias);

        Matrix output = Matrix.matrixMultiply(weightMatrix, inputs);
        output.add(biasMatrix);
        output.map(x -> sigmoid(x));

        return output;
    }

    public Matrix feedForward(Matrix inputs)
    {
        //I -> H
        //Weighted Sum
        this.hiddenOut = Matrix.matrixMultiply(this.weightsIH, inputs);
        //Apply Bias
        this.hiddenOut.add(this.biasH);
        //Apply Activation Function (returns a double between 0 - 1)
        this.hiddenOut.map(x -> sigmoid(x));

        //H -> O
        //Weight Sum
        this.outputOut = Matrix.matrixMultiply(this.weightsHO, this.hiddenOut);
        //Apply Bias
        this.outputOut.add(this.biasO);
        //Apply Activation Function (return a double between 0 - 1)
        this.outputOut.map(x -> sigmoid(x));

        return this.outputOut;
    }

    public void train(Matrix inputs, Matrix targets)
    {
        feedForward(inputs);

        //Cost function??
        Matrix outputErrors = Matrix.subtract(targets, this.outputOut);

        //Gradient = s(x) * (1 - s(x))
        //These calculations hinge on the chain rule
        //where C(l)/W(l) = (z(l)/w(l))*(a(l)/z(l))*(c(l)/a(l))
        //                = a(l-1)*s'(z(l))*2(a(l) - y)
        //Calculate output-gradient
        Matrix outputGradient = Matrix.map(this.outputOut, x-> dSigmoid(x)); //s'(z(l))
        outputGradient.multiply(outputErrors); //(a(l) - y) [Hadamard product]
        outputGradient.scalerMultiply(this.learningRate); //Regulating steps down the gradient

        //Calculate delta weights for weightsHO
        Matrix tHidden = Matrix.transpose(this.hiddenOut);
        Matrix deltaWeightsHO = Matrix.matrixMultiply(outputGradient, tHidden); //a(l-1)
        deltaWeightsHO.multiply(this.velocityHO);
        //V(t) = sigmoid(W(t-1))
        this.velocityHO = Matrix.deepCopy(deltaWeightsHO);
        this.velocityHO.map(x -> sigmoid(x));
        //System.out.println(this.velocityHO);

        //Apply changes to weightsHO
        this.weightsHO.add(deltaWeightsHO);
        //Apply changes to biasO
        this.biasO.add(outputGradient);

        //Calculate hidden layer errors
        Matrix tWeightsHO = Matrix.transpose(this.weightsHO); 
        Matrix hiddenErrors = Matrix.matrixMultiply(tWeightsHO, outputErrors);

        //Calculate hidden-gradient
        Matrix hiddenGradient = Matrix.map(this.hiddenOut, x -> dSigmoid(x));
        hiddenGradient.multiply(hiddenErrors); //Hadamard product
        hiddenGradient.scalerMultiply(this.learningRate);

        //Calculate delta weights for weightIH
        Matrix tInputs = Matrix.transpose(inputs);
        Matrix deltaWeightsIH = Matrix.matrixMultiply(hiddenGradient, tInputs);
        deltaWeightsIH.multiply(this.velocityIH);
        //V(t) = sigmoid(W(t-1))
        this.velocityIH = Matrix.deepCopy(deltaWeightsIH);
        this.velocityIH.map(x -> sigmoid(x));

        //Apply changes to weightsIH
        this.weightsIH.add(deltaWeightsIH);
        //Apply changes to biasH
        this.biasH.add(hiddenGradient);
    }

    private double sigmoid(double x)
    {
        return 1 / (1 + Math.exp(-x));
    }

    private double dSigmoid(double x)
    {
        return x * (1 - x);
    }

    public Matrix getHiddenOut() { return this.hiddenOut; }
    public Matrix getOutputOut() { return this.outputOut; }
    public Matrix getWeightsIH() { return this.weightsIH; }
    public Matrix getWeightsHO() { return this.weightsHO; }
    public Matrix getBiasH() { return this.biasH; }
    public Matrix getBiasO() { return this.biasO; }

    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
}