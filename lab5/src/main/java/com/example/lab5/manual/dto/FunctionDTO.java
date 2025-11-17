package com.example.lab5.manual.dto;


public class FunctionDTO {
    private Long id;
    private Long userId;
    private String name;
    private String signature;


    public FunctionDTO() {}

    public FunctionDTO(Long userId, String name, String signature) {
        this.userId = userId;
        this.name = name;
        this.signature = signature;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    @Override
    public String toString() {
        return "FunctionDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
