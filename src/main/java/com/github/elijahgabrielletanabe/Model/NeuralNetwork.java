package com.github.elijahgabrielletanabe.Model;

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

        this.learningRate = 0.01;
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

        //Cost function??
        Matrix outputErrors = Matrix.subtract(targets, outputs);

        //Gradient = s(x) * (1 - s(x))
        //These calculations hinge on the chain rule
        //where C(l)/W(l) = (z(l)/w(l))*(a(l)/z(l))*(c(l)/a(l))
        //                = a(l-1)*s'(z(l))*2(a(l) - y)
        //Calculate output-gradient
        Matrix outputGradient = Matrix.map(outputs, x-> dSigmoid(x)); //s'(z(l))
        outputGradient.multiply(outputErrors); //(a(l) - y) [Hadamard product]
        outputGradient.scalerMultiply(this.learningRate); //Regulating steps down the gradient

        //Calculate delta weights for weightsHO
        Matrix tHidden = Matrix.transpose(hidden);
        Matrix deltaWeightsHO = Matrix.matrixMultiply(outputGradient, tHidden); //a(l-1)

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
        Matrix tInputs = Matrix.transpose(inputs);
        Matrix deltaWeightsIH = Matrix.matrixMultiply(hiddenGradient, tInputs);

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

    public Matrix getWeightsIH() { return this.weightsIH; }
    public Matrix getWeightsHO() { return this.weightsHO; }
    public Matrix getBiasH() { return this.biasH; }
    public Matrix getBiasO() { return this.biasO; }

    public void setLearningRate(double learningRate) { this.learningRate = learningRate; }
}