package gladun.vladimir.nzdrivingtest;

import java.util.ArrayList;

/**
 * Question entity
 *
 * @author Vladimir Gladun vvgladoun@gmail.com
 */
public class Question {

    private int id;
    private String questionText;
    private int categoryId;
    private ArrayList<Answer> answers;
    private boolean multipleChoice;
    private String imageName;
    private int testType;
    private String explanation;
    private String explanationImage;

    // values of the testType field
    public static final int CAR_TEST = 1;
    public static final int MOTORBIKE_TEST = 2;

    /**
     * Question constructor method
     *
     * params are all attributes of the class
     *
     * @param id - question id in the data store
     * @param questionText - text of the question
     * @param categoryId - test category
     * @param answers - array of possible answers
     * @param multipleChoice - true if multiple choice question
     * @param imageName - name of the drawable for the question
     * @param testType - type: Car test, Motorbike test etc.
     */
    public Question(int id, String questionText, int categoryId,
                    ArrayList<Answer> answers, boolean multipleChoice,
                    String imageName, int testType,
                    String explanation, String explanationImage){

        this.id = id;
        this.questionText = questionText;
        this.categoryId = categoryId;
        this.answers = answers;
        this.multipleChoice = multipleChoice;
        this.imageName = imageName;
        this.testType = testType;
        this.explanation = explanation;
        this.explanationImage = explanationImage;
    }

    /**
     *
     * @return array list of posible answers
     */
    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    /**
     * Add answer to question's answer array
     *
     * @param answer Answer object to add
     */
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    /**
     *
     * @return question category id
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     *
     * @return question id in the database
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return image name (to get from drawables)
     */
    public String getImageName() {
        return imageName;
    }

    /**
     *
     * @return true if question has more then one right answer
     */
    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    /**
     *
     * @return text of the question
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     *
     * @return type (car/motorbike/...)
     */
    public int getTestType() {
        return testType;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getExplanationImage() {
        return explanationImage;
    }

}
