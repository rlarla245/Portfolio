package com.test.howlfirebasetest1_1;

import java.util.HashMap;
import java.util.Map;

public class ImageDTO {
    public String imageUri;
    public String imageName;
    public String title;
    public String description;
    public String uid;
    public String userId;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
}
