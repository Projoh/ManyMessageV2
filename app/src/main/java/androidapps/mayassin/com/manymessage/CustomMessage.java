package androidapps.mayassin.com.manymessage;

/**
 * Created by moham on 3/11/2017.
 */

public class CustomMessage {

   String message = "Hey fname, I'm trying to contact the lname family. We are having a new event that involves variable1 and is sponsored by variable2. What do you think?",
           variableOne="Major Sports Academy",
           varibaleTwo="University of Popular Football team";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVariableOne() {
        return variableOne;
    }

    public void setVariableOne(String variableOne) {
        this.variableOne = variableOne;
    }

    public String getVaribaleTwo() {
        return varibaleTwo;
    }

    public void setVaribaleTwo(String varibaleTwo) {
        this.varibaleTwo = varibaleTwo;
    }

    public CustomMessage() {

    }
}
