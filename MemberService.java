package com.library.app.service;

import com.library.app.db.MemberDAO;
import com.library.app.model.Member;
import com.library.app.util.Logger;

import java.util.Date;
import java.util.List;


public class MemberService {
    
    private static final Logger logger = new Logger(MemberService.class.getName());
    private final MemberDAO memberDAO;
    

    public MemberService() {
        this.memberDAO = new MemberDAO();
    }
    

    public Member getMemberById(int id) throws Exception {
        try {
            return memberDAO.getMemberById(id);
        } catch (Exception e) {
            logger.error("Error getting member by ID: " + id, e);
            throw new Exception("Error retrieving member: " + e.getMessage());
        }
    }
    

    public List<Member> getAllMembers() throws Exception {
        try {
            return memberDAO.getAllMembers();
        } catch (Exception e) {
            logger.error("Error getting all members", e);
            throw new Exception("Error retrieving members: " + e.getMessage());
        }
    }
    

    public List<Member> getActiveMembers() throws Exception {
        try {
            return memberDAO.getActiveMembers();
        } catch (Exception e) {
            logger.error("Error getting active members", e);
            throw new Exception("Error retrieving active members: " + e.getMessage());
        }
    }
    

    public int addMember(Member member) throws Exception {
        try {
            // Validate input
            if (member.getName() == null || member.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Member name cannot be empty");
            }
            
            if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Member email cannot be empty");
            }
            
            if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
                throw new IllegalArgumentException("Member phone cannot be empty");
            }
            
            // Check if email already exists
            List<Member> existingMembers = memberDAO.searchMembersByEmail(member.getEmail());
            if (!existingMembers.isEmpty()) {
                throw new IllegalArgumentException("Email already exists in the system");
            }
            
            // Set default values if not provided
            if (member.getJoinDate() == null) {
                member.setJoinDate(new Date());
            }
            
            if (member.getRole() == null || member.getRole().trim().isEmpty()) {
                member.setRole("REGULAR");
            }
            
            if (member.getStatus() == null || member.getStatus().trim().isEmpty()) {
                member.setStatus("ACTIVE");
            }
            
            return memberDAO.addMember(member);
        } catch (Exception e) {
            logger.error("Error adding member: " + member.getName(), e);
            throw new Exception("Error adding member: " + e.getMessage());
        }
    }
    

    public void updateMember(Member member) throws Exception {
        try {
            // Validate input
            if (member.getId() <= 0) {
                throw new IllegalArgumentException("Invalid member ID");
            }
            
            if (member.getName() == null || member.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Member name cannot be empty");
            }
            
            if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
                throw new IllegalArgumentException("Member email cannot be empty");
            }
            
            if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
                throw new IllegalArgumentException("Member phone cannot be empty");
            }
            
            // Check if email already exists for another member
            List<Member> existingMembers = memberDAO.searchMembersByEmail(member.getEmail());
            for (Member existingMember : existingMembers) {
                if (existingMember.getId() != member.getId()) {
                    throw new IllegalArgumentException("Email already exists for another member");
                }
            }
            
            memberDAO.updateMember(member);
        } catch (Exception e) {
            logger.error("Error updating member: " + member.getName(), e);
            throw new Exception("Error updating member: " + e.getMessage());
        }
    }
    

    public void deleteMember(int id) throws Exception {
        try {
            // Check if the member exists
            Member member = memberDAO.getMemberById(id);
            if (member == null) {
                throw new IllegalArgumentException("Member does not exist");
            }
            
            // Check if the member has borrowed books
            if (member.getBorrowedCount() > 0) {
                throw new IllegalStateException("Cannot delete member with borrowed books");
            }
            
            memberDAO.deleteMember(id);
        } catch (Exception e) {
            logger.error("Error deleting member with ID: " + id, e);
            throw new Exception("Error deleting member: " + e.getMessage());
        }
    }
    

    public List<Member> searchMembersById(int id) throws Exception {
        try {
            return memberDAO.searchMembersById(id);
        } catch (Exception e) {
            logger.error("Error searching members by ID: " + id, e);
            throw new Exception("Error searching members: " + e.getMessage());
        }
    }
    

    public List<Member> searchMembersByName(String name) throws Exception {
        try {
            return memberDAO.searchMembersByName(name);
        } catch (Exception e) {
            logger.error("Error searching members by name: " + name, e);
            throw new Exception("Error searching members: " + e.getMessage());
        }
    }
    

    public List<Member> searchMembersByEmail(String email) throws Exception {
        try {
            return memberDAO.searchMembersByEmail(email);
        } catch (Exception e) {
            logger.error("Error searching members by email: " + email, e);
            throw new Exception("Error searching members: " + e.getMessage());
        }
    }
    

    public List<Member> searchMembersByPhone(String phone) throws Exception {
        try {
            return memberDAO.searchMembersByPhone(phone);
        } catch (Exception e) {
            logger.error("Error searching members by phone: " + phone, e);
            throw new Exception("Error searching members: " + e.getMessage());
        }
    }
    

    public List<Member> getMembersWithOverdueBooks() throws Exception {
        try {
            return memberDAO.getMembersWithOverdueBooks();
        } catch (Exception e) {
            logger.error("Error getting members with overdue books", e);
            throw new Exception("Error retrieving members with overdue books: " + e.getMessage());
        }
    }
    
    // Fine-related methods have been removed as part of the removal of the fines system
    

    public List<Member> getRecentMembers(int limit) throws Exception {
        try {
            return memberDAO.getRecentMembers(limit);
        } catch (Exception e) {
            logger.error("Error getting recent members", e);
            throw new Exception("Error retrieving recent members: " + e.getMessage());
        }
    }
}
