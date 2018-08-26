package com.example.t1g.firebaseproject;

public class Profile {

    private String id;
    private String name;
    private String gender;
    private String age;
    private String hobbies;
    private String image;

    Profile(){
    }

    Profile(String id, String name, String gender, String age, String hobbies, String image){
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.hobbies = hobbies;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
