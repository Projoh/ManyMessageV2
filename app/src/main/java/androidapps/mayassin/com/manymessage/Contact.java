package androidapps.mayassin.com.manymessage;

/**
 * Created by moham on 3/9/2017.
 */

public class Contact {
    public String firstName, lastName, phoneNumber;
    boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Contact(String name, String phone) {
        if(name.split("\\w+").length>1){
            lastName = name.substring(name.lastIndexOf(" ") > -1 ? name.lastIndexOf(" ")+1 : name.length() - 1);
            firstName = name.substring(0, name.lastIndexOf(' ') > -1 ? name.lastIndexOf(' ')  : name.length() - 1);
        }
        else{
            firstName = name;
            lastName = " ";
        }

        phone = phone.replaceAll("\\W+", "");
        char firstchar = phone.charAt(0);
        if(Character.getNumericValue(firstchar)== 1) {
            phone = "+"+phone;
        }else {
            phone = "+1"+phone;
        }

        phoneNumber = phone;
    }
}
