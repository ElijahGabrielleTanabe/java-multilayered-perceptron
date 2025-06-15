# Java Multilayered Perceptron

A multilayered perceptron neural network in Java. This project uses Apache Maven as the build tool and JavaFX as the GUI with the help of Gluon's SceneBuilder. This project does not require the JDK or Apache Maven to be run. <br/>

This neural network is aimed at solving the non-linearly seperable problem XOR, using sigmoid as the activation function and stochastic gradient descent with a learning rate in backpropogation to adjust the weights and biases. *Please take these words with a grain of salt as I am yet well versed in this*

## Table of Contents

- [**How to run**](#how-to-run)
- [**How it works**](#how-it-works)
- [**Notes**](#notes)
- [**Project motive and thoughts**](#project-motive)
- [**Credits**](#credits)
- [**To-do**](#to-do)

## How to run

#### With Image
1) Download the image from this [branch](https://github.com/ElijahGabrielleTanabe/java-multilayered-perceptron/tree/v1.0.x).
2) Unzip the file and locate the bin folder within the image.
3) Run the launch.bat file.
> This image was created with Jlink and contains a JRE, the necessary dependencies, and the source files. Feel free to verify its contents before you run it

#### With JDK 17+ Installed
1) Download the full Maven project in the [main branch](https://github.com/ElijahGabrielleTanabe/java-multilayered-perceptron/tree/main).
2) Open a terminal and cd into the project directory at the same level as the pom.xml file.
3) With the Maven Wrapper run the command `mvnw.cmd clean javafx:run` for windows command prompt or `./mvnw clean javafx:run` for everything else.
4) After you are done, you can delete the .m2 and cache folders that contains the artifacts and other files that were downloaded.

| Operating System | .m2 directory | Cache directory |
| ---------------- | ------------- | --------------- |
| Windows | C:\Users\\<username\>\\.m2 | C:\Users\\<username\>\AppData\Local |
| Linux | /home/\<username\>/.m2 | /home/\<username\>/.cache |
| Mac | /Users/\<username\>/.m2 | /Users/\<username\>/.cache |

## How it works

The main window has a grayscale canvas that represents the output of the network which ranges from black (0) to white (1). The network takes the coordinates of the canvas as input starting at (0, 0) at the top left corner to (1, 1) at the bottom right corner. The right side panel has controls for the network and canvas as well as a button to view the nodes and weights for the network. <br>

![MainWindow](https://github.com/ElijahGabrielleTanabe/java-multilayered-perceptron/blob/gifs/MainWindow.gif)

The node view window visualizes the layers of the network with the weights that connect the nodes together. Each weight line gets bigger and more vibrant as the its influence on the node grows stronger. The colour's represent the weights value, red being less than 0 and blue being greater than 0. You can further examine how each hidden node contributes to the output node by clicking on a hidden node. <br>

![NodeView](https://github.com/ElijahGabrielleTanabe/java-multilayered-perceptron/blob/gifs/NodeView.gif)

The heatmap view shows the hidden node's activation output based on the x and y coordinates of the canvas starting at (0, 0) at the top left corner to (1, 1) at the bottom right. The hidden node's output corrusponds with the colour's, ranging from 0 (red) to 1 (blue).

![Heatmap](https://github.com/ElijahGabrielleTanabe/java-multilayered-perceptron/blob/gifs/Heatmap.gif)

## Notes

- Currently the biases effects on the network are not visualized, in the future I will add it along side the weights.
- This network has some what of a hard time finding the global minima and getting out of local minima's which is something I will attempt to fix with velocity and a different activation function.

## Project motive and thoughts

With all the attention on artifical intelligence, machine learning, and large language models, it's never been a more important time to learn about the technology that heavily influences the tools and the jobs of present and future generations. So, in an attempt to get ahead of the curve and not get left behind, I spent two weeks dipping my toes into the world of machine learning and neural networks. <br>

This was perhaps the project I was looking forward to the most this month and I chose to do it in Java. Not only because I plan on learning and understanding Java's more advanced elements and niche aspects, but also because I really wanted to understand the code and its mechanics on a deeper level, and Python with its amazing assortment of libraries for just this topic seemed way too easy. Personally, I've never been good at understanding complex mathematics (all the symbols and variables just don't seem to connect with me) and this learning adventure proved that. Like with learning any new thing, it was hard to grasp the concept the first time and I would have to read over the paragraph three times or replay the video from the start. But with enough time, I would have those "aha" moments where it just clicks and I begin to understand why people love learning. <br>

What I ended up learning was pretty much surface level in this massive field that is still being researched and studied. Still, it gave me a good sense of my learning capabilities and the type of topics that intrigued me. Overall, this project has been a great experience, leading me to many rabbit holes, waking me up at ungodly hours, and working till the sun goes down. In the future I plan on adapting this project for the Hello World of machine learning, the handwritten digits of the MNIST database, and by that time I hope I can look back on this project and say "Wow, that's some garbage code" (_as if im not saying it already_).<br>

If you read all that, thank you and goodbye!

## Credits

#### Neural Network & Machine Learning
##### [The Nature of Code JS Neural Network Series](https://www.youtube.com/playlist?list=PLZHQObOWTQDNU6R1_67000Dx_ZCJB-3pi)
- The basis for the neural network that this project implements. Excellent series for those who want a slower, step by step guide with lots of explanations. This series makes everything from scratch, starting from a single perceptron, moving to a multilayered perceptron with a small matrix library. Highly recommend for Javascript and Java.
##### [3Blue1Brown's Neural Network Series](https://www.youtube.com/playlist?list=PLRqwX-V7Uu6aCibgK1PTWWu9by6XFdCfh)
- First video I watched on neural networks, even without past knowledge it was easily understandable. The amazing animation conveys the information clearly and consisely with little to no distractions. On top of that, he offers several explanations for each topic of interest, breaking them down into simpler and simpler terms to better digest the information. Big thumbs up for this one.
##### [Michael Nielson's Neural Network with Handwritten Digits](https://neuralnetworksanddeeplearning.com/chap1.html)
- A thorough explanation of the various parts that make up a neural network, offering various practical examples to understand how each part connects with one another. Goes into a great detail about the mathematical concepts that are present and has code examples at the end to play around with. (_Honestly I didn't read the whole thing, but for the parts I did it was great_)

#### Derivatives & Chain Rule
##### [3Blue1Brown's Essence of Calculus](https://www.youtube.com/playlist?list=PLZHQObOWTQDMsr9K-rj53DwVRMYO3t5Yr)
- Again, amazing visuals and excellent explanations (this guy was born to be a teacher). This does require some Calculus 1 knowledge which I didn't have, but even with my small brain it was quite understandable and the videos build onto one another which is nice. (**3Blue1Brown the GOAT of Math Animations**)
##### [The Organic Chemistry Tutor's Calc 1 Derivatives](https://www.youtube.com/watch?v=5yfh5cf4-0w&t=2059s&pp=ygULZGVyaXZhdGl2ZXM%3D)
- Great explanations on derivatives and how one would calculate a derivative of a function. Plenty of practice questions with lots of a variety so you ingrain the topic. Coming from a person _just_ learning derivatives, pretty good.
##### [The Organic Chemistry Tutor's Calc 1 Chain Rule](https://www.youtube.com/watch?v=HaHsqDjWMLU&t=228s&pp=ygULZGVyaXZhdGl2ZXM%3D)
- Needed to learn this to understand how backpropagation works. Great explanations, lots of practice questions. Overall it did help me understand the process better, 9/10.
##### [Khan Academy's Derivative as a Concept](https://www.youtube.com/watch?v=N2PpRnFqnqY&pp=ygULZGVyaXZhdGl2ZXM%3D)
- Probably the best explanation of derivatives (tied with that one stackoverflow question) by first examining the regular rise over run formula then examining derivatives on a conceptual manner.

## To-do

- [ ] **Velocity:** Carry over previous epoch's speed to get over small dips in the gradient.
- [ ] **Bias Lines:** Visual for the bias in the node view.
- [ ] **Hidden Node Visual:** Zoom function for node view, currently overflows when there's more than 5 nodes.
