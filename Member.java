package com.library.app.model;

import java.util.Date;
import java.util.Objects;


public class Member {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Date joinDate;
    private Date expiryDate;
    private String role; // REGULAR, PREMIUM, ADMIN
    private String status; // ACTIVE, INACTIVE, SUSPENDED
    private String password;
    private int borrowedCount;
    private String profileImage;
    

    public Member() {
        this.joinDate = new Date();
        this.role = "REGULAR";
        this.status = "ACTIVE";
        this.borrowedCount = 0;
    }
    

    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.joinDate = new Date();
        this.role = "REGULAR";
        this.status = "ACTIVE";
        this.borrowedCount = 0;
    }
    

    public Member(int id, String name, String email, String phone, String address, 
                 Date joinDate, Date expiryDate, String role, String status, 
                 String password, int borrowedCount, String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.joinDate = joinDate;
        this.expiryDate = expiryDate;
        this.role = role;
        this.status = status;
        this.password = password;
        this.borrowedCount = borrowedCount;
        this.profileImage = profileImage;
    }

    // Getters and Setters
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getBorrowedCount() {
        return borrowedCount;
    }

    public void setBorrowedCount(int borrowedCount) {
        this.borrowedCount = borrowedCount;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id ||
               (email != null && email.equals(member.email));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Member{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", email='" + email + '\'' +
               ", role='" + role + '\'' +
               ", status='" + status + '\'' +
               ", borrowedCount=" + borrowedCount +
               '}';
    }


    public Member copy() {
        Member copy = new Member();
        copy.id = this.id;
        copy.name = this.name;
        copy.email = this.email;
        copy.phone = this.phone;
        copy.address = this.address;
        copy.joinDate = this.joinDate != null ? new Date(this.joinDate.getTime()) : null;
        copy.expiryDate = this.expiryDate != null ? new Date(this.expiryDate.getTime()) : null;
        copy.role = this.role;
        copy.status = this.status;
        copy.password = this.password;
        copy.borrowedCount = this.borrowedCount;
        copy.profileImage = this.profileImage;
        return copy;
    }


    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return new Date().after(expiryDate);
    }


    public int getMaxAllowedBorrows() {
        if ("PREMIUM".equalsIgnoreCase(role)) {
            return 10;
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            return 15;
        } else {
            return 5; // REGULAR members
        }
    }


    public boolean canBorrowMore() {
        return borrowedCount < getMaxAllowedBorrows() && 
               "ACTIVE".equalsIgnoreCase(status) && 
               !isExpired();
    }
    

    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(status) && !isExpired();
    }
}