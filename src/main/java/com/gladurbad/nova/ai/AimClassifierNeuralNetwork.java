package com.gladurbad.nova.ai;

import com.gladurbad.nova.util.mouse.MouseSnapshot;
import org.deeplearning4j.datasets.iterator.utilty.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AimClassifierNeuralNetwork {

    private static final int BATCH_SIZE = 32;
    private final MultiLayerNetwork model;
    private final List<MouseSnapshot> trainingMouseSnapshots = new ArrayList<>();
    private final List<MouseSnapshot> predictingMouseSnapshots = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AimClassifierNeuralNetwork() {
        // Configure the neural network architecture
        int seed = 12345;
        int numInputs = 7; // Number of input features
        int numOutputs = 2; // Binary classification (aimbot vs. human)
        int numHidden = 50; // Number of neurons in the hidden layer

        MultiLayerConfiguration builder = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .updater(new Adam(1e-3))
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(0, new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(numHidden)
                        .activation(Activation.RELU)
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nIn(numHidden)
                        .nOut(numOutputs)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backpropType(BackpropType.Standard)
                .build();

        model = new MultiLayerNetwork(builder);
        model.init();
        model.setListeners(new ScoreIterationListener(10));
    }

    public void trainSnapshot(MouseSnapshot mouseSnapshot, int label) {
        this.trainingMouseSnapshots.add(mouseSnapshot);

        if (trainingMouseSnapshots.size() == BATCH_SIZE) {
            executor.execute(() -> train(trainingMouseSnapshots, 1000, label));
            trainingMouseSnapshots.clear();
        }
    }

    public void predictSnapshot(MouseSnapshot snapshot) {
        this.predictingMouseSnapshots.add(snapshot);

        if (predictingMouseSnapshots.size() == BATCH_SIZE) {
            predict(predictingMouseSnapshots);
            predictingMouseSnapshots.clear();
        }
    }

    private void train(List<MouseSnapshot> mouseSnapshots, int epochs, int label) {
        DataSetIterator iterator = convertToIterator(mouseSnapshots);

        while (iterator.hasNext()) {
            DataSet dataSet = iterator.next();
            INDArray labels = Nd4j.valueArrayOf(1, label);
            dataSet.setLabels(labels);
        }

        for (int i = 0; i < epochs; i++) {
            iterator.reset();
            model.fit(iterator);
        }
    }

    private void predict(List<MouseSnapshot> mouseSnapshots) {
        executor.execute(() -> {
            int batchSize = mouseSnapshots.size();
            int numInputs = 7; // Number of input features

            INDArray inputs = Nd4j.zeros(batchSize, numInputs);

            // Populate input array
            for (int i = 0; i < batchSize; i++) {
                MouseSnapshot snapshot = mouseSnapshots.get(i);
                inputs.putScalar(i, 0, snapshot.getDeltaYaw());
                inputs.putScalar(i, 1, snapshot.getDeltaPitch());
                inputs.putScalar(i, 2, snapshot.getAccelerationYaw());
                inputs.putScalar(i, 3, snapshot.getAccelerationPitch());
                inputs.putScalar(i, 4, snapshot.getInterceptX());
                inputs.putScalar(i, 5, snapshot.getInterceptY());
                inputs.putScalar(i, 6, snapshot.getOffsetFromCenter());
            }

            INDArray inputArray = inputs.transpose();

            // Perform prediction
            INDArray predictions = model.output(inputArray, false);

            System.out.println("Predictions for the batch:");
            System.out.println(predictions);
        });
    }

    private static DataSetIterator convertToIterator(List<MouseSnapshot> mouseSnapshots) {
        // Convert MouseSnapshots to a list of DataSet
        List<DataSet> dataSets = convertMouseSnapshotsToDataSets(mouseSnapshots);

        return new ListDataSetIterator<>(dataSets, BATCH_SIZE);
    }

    private static List<DataSet> convertMouseSnapshotsToDataSets(List<MouseSnapshot> mouseSnapshots) {
        List<DataSet> dataSets = new ArrayList<>();
        for (MouseSnapshot snapshot : mouseSnapshots) {
            // Convert each MouseSnapshot to a DataSet
            double[] array = {
                    snapshot.getDeltaYaw(),
                    snapshot.getDeltaPitch(),
                    snapshot.getAccelerationYaw(),
                    snapshot.getAccelerationPitch(),
                    snapshot.getInterceptX(),
                    snapshot.getInterceptY(),
                    snapshot.getOffsetFromCenter()
            };
            INDArray features = Nd4j.create(array);
            INDArray labels = Nd4j.zeros(1); // Example: All labels are set to 0
            dataSets.add(new DataSet(features, labels));
        }
        return dataSets;
    }
}

