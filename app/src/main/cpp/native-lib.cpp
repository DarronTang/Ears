#include "opencv2/objdetect.hpp"
#include "opencv2/videoio.hpp"
#include "opencv2/highgui.hpp"
#include "opencv2/imgproc.hpp"
#include "opencv2/face.hpp"

#include <jni.h>
#include <android/log.h>
#include <fstream>
#include <iostream>

using namespace std;
using namespace cv;
using namespace face;


const char* TAG = "Native-Darron";

/* Function Headers */
int detect_and_display(Mat frame, Ptr<FaceRecognizer> model);
static void read_csv(
        const string& filename,
        vector<Mat>& images,
        vector<int>& labels,
        char separator = ','
);
int ear_rec(const string csv_path,
            const string haar_left_path,
            const string haar_right_path,
            const string input_image
);
//void showPrediction

/* Global variables */
string leftear_cascade_name, rightear_cascade_name;
CascadeClassifier leftear_cascade, rightear_cascade;

/**
 *
 * @param frame
 * @param model
 * @return
 */
int detect_and_display(Mat frame, Ptr<FaceRecognizer> model)
{
    std::vector<Rect> leftears;
    std::vector<Rect> rightears;
    Mat frame_gray;
    int predicted_label = -1;
    double predicted_confidence = 0.0;

    cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    //-- Detect left ear
    leftear_cascade.detectMultiScale(frame_gray, leftears, 1.05, 2,
                                     0 | CASCADE_SCALE_IMAGE, Size(30, 30));

    //-- Detect right ear
    rightear_cascade.detectMultiScale(frame_gray, rightears, 1.05, 2,
                                      0 | CASCADE_SCALE_IMAGE, Size(30, 30));
    for (size_t i = 0; i < leftears.size(); i++)
    {
        Mat leftear = frame(leftears[i]);
        resize(leftear, leftear, Size(250, 400));
        cvtColor(leftear, leftear, CV_BGR2GRAY);
        GaussianBlur(leftear, leftear, Size(7,7), 0, 0 );
        model->predict(leftear, predicted_label, predicted_confidence);
    }
    //-- Show what you got
    return predicted_label;
}

/**
 * Reads the CSV file that contains ear data
 * @param filename Filename of the CSV file containing the ear data
 * @param images Images of ears, as vector<Mat>
 * @param labels Labels for the ears as vector<int>
 * @param separator The delimiter for the CSV file
 */
static void read_csv(
        const string& filename,
        vector<Mat>& images,
        vector<int>& labels,
        char separator
) {
    std::ifstream file(filename.c_str(), ifstream::in);
    if (!file) {
        string error_message = "No valid input file was given, please check the given filename.";
        CV_Error(CV_StsBadArg, error_message);
    }
    string line, path, classlabel, name;
    while (getline(file, line)) {
        stringstream liness(line);
        getline(liness, classlabel, separator);
        getline(liness, name, separator);
        getline(liness, path);
        if (!path.empty() && !classlabel.empty()) {
            images.push_back(imread(path, 0));
            labels.push_back(atoi(classlabel.c_str()));
        }
    }
}


int ear_rec(
        const string csv_path,
        const string haar_left_path,
        const string haar_right_path,
        const string input_image
) {
    // These vectors hold the images and corresponding labels.
    vector<Mat> trainingImages;
    vector<int> labels;
    // Read in the data. This can fail if no valid
    // input filename is given.
    try {
        read_csv(csv_path, trainingImages, labels);
    }
    catch (cv::Exception& e) {
        char* error_message = (char*)malloc(1000 * sizeof(char));
        snprintf(error_message, 1000, "Error opening file \"%s\". Reason: %s", csv_path.c_str(), e.msg.c_str());
        __android_log_print(ANDROID_LOG_ERROR, TAG, "%s", error_message);
        cerr << error_message << endl;
        free(error_message);
        // nothing more we can do
        exit(1);
    }

    // Quit if there are not enough images for this demo.
    if (trainingImages.size() <= 1) {
        string error_message = "This demo needs at least 2 images to work. Please add more images to your data set!";
        __android_log_print(ANDROID_LOG_ERROR, TAG, "%s", error_message.c_str());
        CV_Error(CV_StsError, error_message);
    }

    for (int i = 0; i < trainingImages.size() ; i++)
    {
        GaussianBlur(trainingImages[i], trainingImages[i], Size(7, 7), 0, 0);
    }

    Ptr<FaceRecognizer> model = createLBPHFaceRecognizer();
    model->train(trainingImages, labels);

    leftear_cascade_name = haar_left_path;
    rightear_cascade_name = haar_right_path;
    Mat frame;

    //-- 1. Load the cascades
    if (!leftear_cascade.load(leftear_cascade_name)) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error loading LE cascade\n");
        return -1;
    };
    if (!rightear_cascade.load(rightear_cascade_name)) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Error loading RE cascade\n");
        return -1;
    };

    //-- 2. Read the image
    frame = imread(input_image);// passed in image;

    //-- 3. Apply the classifier to the frame
    int predicted_label = detect_and_display(frame, model);

    return predicted_label;
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_ears_advcomp_ears3_FindEarActivity_earRec(
        JNIEnv* env,
        jobject /* this */,
        jstring csvPath,
        jstring haarLeftPath,
        jstring haarRightPath,
        jstring inputImage
) {
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Starting ear recognition");
    // Allocate memory for input strings
    const string csv_path = env->GetStringUTFChars(csvPath, 0);
    const string haar_left_path = env->GetStringUTFChars(haarLeftPath, 0);
    const string haar_right_path = env->GetStringUTFChars(haarRightPath, 0);
    const string input_image = env->GetStringUTFChars(inputImage, 0);
    // Compute
    int person = ear_rec(csv_path, haar_left_path, haar_right_path, input_image);
    __android_log_print(ANDROID_LOG_DEBUG, TAG, "Done calculating on the C++ end, returning.");
    // Release memory for input strings
    env->ReleaseStringUTFChars(csvPath, csv_path.c_str());
    env->ReleaseStringUTFChars(haarLeftPath, haar_left_path.c_str());
    env->ReleaseStringUTFChars(haarRightPath, haar_right_path.c_str());
    env->ReleaseStringUTFChars(inputImage, input_image.c_str());
    return person;
}
