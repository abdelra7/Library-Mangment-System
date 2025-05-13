package com.library.app.db;

import com.library.app.model.Member;
import com.library.app.util.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class MemberDAO {
    
    private static final Logger logger = new Logger(MemberDAO.class.getName());


    public Member getMemberById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Member member = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                member = mapResultSetToMember(rs);
            }
            
            logger.info("Retrieved member by ID: " + id);
            return member;
        } catch (SQLException e) {
            logger.error("Error retrieving member by ID: " + id, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Member> getAllMembers() throws SQLException {
        String sql = "SELECT * FROM members ORDER BY name";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            // Update the borrowed count for each member from the transactions table
            updateBorrowedCounts(members, conn);
            
            logger.info("Retrieved all members: " + members.size() + " records");
            return members;
        } catch (SQLException e) {
            logger.error("Error retrieving all members", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }

    private void updateBorrowedCounts(List<Member> members, Connection conn) {
        if (members.isEmpty()) {
            return;
        }
        
        String sql = "SELECT member_id, COUNT(*) as book_count FROM transactions " +
                    "WHERE return_date IS NULL GROUP BY member_id";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            // Create a map for quick lookup of borrowed counts
            java.util.Map<Integer, Integer> borrowedCountMap = new java.util.HashMap<>();
            
            while (rs.next()) {
                int memberId = rs.getInt("member_id");
                int bookCount = rs.getInt("book_count");
                borrowedCountMap.put(memberId, bookCount);
            }
            
            // Update each member's borrowed count
            for (Member member : members) {
                Integer borrowedCount = borrowedCountMap.get(member.getId());
                member.setBorrowedCount(borrowedCount != null ? borrowedCount : 0);
            }
            
            logger.info("Updated borrowed counts for " + members.size() + " members");
        } catch (SQLException e) {
            logger.error("Error updating borrowed counts for members", e);
            // Don't throw the exception - this is a supplementary operation
        }
    }
    

    public List<Member> getActiveMembers() throws SQLException {
        String sql = "SELECT * FROM members WHERE status = 'ACTIVE' ORDER BY name";
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            logger.info("Retrieved active members: " + members.size() + " records");
            return members;
        } catch (SQLException e) {
            logger.error("Error retrieving active members", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public int addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, email, phone, address, join_date, " +
                     "expiry_date, role, status, password, borrowed_count) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getAddress());
            stmt.setTimestamp(5, member.getJoinDate() != null ? 
                              new Timestamp(member.getJoinDate().getTime()) : null);
            stmt.setTimestamp(6, member.getExpiryDate() != null ? 
                              new Timestamp(member.getExpiryDate().getTime()) : null);
            stmt.setString(7, member.getRole());
            stmt.setString(8, member.getStatus());
            stmt.setString(9, member.getPassword());
            stmt.setInt(10, member.getBorrowedCount());
//            stmt.setString(11, member.getProfileImage());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating member failed, no rows affected.");
            }
            
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                member.setId(id);
                logger.info("Added new member: " + member.getName() + " with ID: " + id);
                return id;
            } else {
                throw new SQLException("Creating member failed, no ID obtained.");
            }
        } catch (SQLException e) {
            logger.error("Error adding member: " + member.getName(), e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }

    public void updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ?, address = ?, " +
                     "join_date = ?, expiry_date = ?, role = ?, status = ?, password = ?, " +
                     "borrowed_count = ? " +
                     "WHERE id = ?";
                     
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getAddress());
            stmt.setTimestamp(5, member.getJoinDate() != null ? 
                              new Timestamp(member.getJoinDate().getTime()) : null);
            stmt.setTimestamp(6, member.getExpiryDate() != null ? 
                              new Timestamp(member.getExpiryDate().getTime()) : null);
            stmt.setString(7, member.getRole());
            stmt.setString(8, member.getStatus());
            stmt.setString(9, member.getPassword());
            stmt.setInt(10, member.getBorrowedCount());
//            stmt.setString(11, member.getProfileImage());
            stmt.setInt(11, member.getId());
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Updated member ID: " + member.getId() + ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error updating member ID: " + member.getId(), e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public void deleteMember(int id) throws SQLException {
        String sql = "DELETE FROM members WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            
            int affectedRows = stmt.executeUpdate();
            logger.info("Deleted member ID: " + id + ", rows affected: " + affectedRows);
        } catch (SQLException e) {
            logger.error("Error deleting member ID: " + id, e);
            throw e;
        } finally {
            closeResources(null, stmt);
        }
    }
    

    public List<Member> searchMembersById(int id) throws SQLException {
        String sql = "SELECT * FROM members WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            logger.info("Search by ID " + id + " results: " + members.size() + " members found");
            return members;
        } catch (SQLException e) {
            logger.error("Error searching members by ID: " + id, e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Member> searchMembersByName(String name) throws SQLException {
        String sql = "SELECT * FROM members WHERE LOWER(name) LIKE LOWER(?) ORDER BY name";
        return searchMembers(sql, "%" + name + "%");
    }
    

    public List<Member> searchMembersByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM members WHERE LOWER(email) LIKE LOWER(?) ORDER BY name";
        return searchMembers(sql, "%" + email + "%");
    }
    

    public List<Member> searchMembersByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM members WHERE LOWER(phone) LIKE LOWER(?) ORDER BY name";
        return searchMembers(sql, "%" + phone + "%");
    }
    

    public List<Member> getMembersWithOverdueBooks() throws SQLException {
        String sql = "SELECT DISTINCT m.* " +
                     "FROM members m " +
                     "JOIN transactions t ON m.id = t.member_id " +
                     "WHERE t.status = 'BORROWED' AND t.due_date < CURRENT_TIMESTAMP " +
                     "ORDER BY m.name";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            logger.info("Retrieved members with overdue books: " + members.size() + " records");
            return members;
        } catch (SQLException e) {
            logger.error("Error retrieving members with overdue books", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    public List<Member> getRecentMembers(int limit) throws SQLException {
        String sql = "SELECT * FROM members ORDER BY join_date DESC LIMIT ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            logger.info("Retrieved recent members: " + members.size() + " records");
            return members;
        } catch (SQLException e) {
            logger.error("Error retrieving recent members", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    private List<Member> searchMembers(String sql, String searchParam) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Member> members = new ArrayList<>();
        
        try {
            conn = DatabaseConnection.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, searchParam);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
            
            logger.info("Search results: " + members.size() + " members found");
            return members;
        } catch (SQLException e) {
            logger.error("Error searching members", e);
            throw e;
        } finally {
            closeResources(rs, stmt);
        }
    }
    

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setName(rs.getString("name"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAddress(rs.getString("address"));
        
        Timestamp joinDate = rs.getTimestamp("join_date");
        if (joinDate != null) {
            member.setJoinDate(new java.util.Date(joinDate.getTime()));
        }
        
        Timestamp expiryDate = rs.getTimestamp("expiry_date");
        if (expiryDate != null) {
            member.setExpiryDate(new java.util.Date(expiryDate.getTime()));
        }
        
        member.setRole(rs.getString("role"));
        member.setStatus(rs.getString("status"));
        
        // Safely retrieve optional fields that might not exist in all database versions
        try {
            member.setPassword(rs.getString("password"));
        } catch (SQLException e) {
            // Password field might not exist
            member.setPassword("");
        }
        
        // Calculate borrowed count from transactions table instead of using a column
        member.setBorrowedCount(0); // Default to 0
        
        // Set default values for fields not in the database
//        member.setProfileImage("");
        
        return member;
    }
    

    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            logger.error("Error closing database resources", e);
        }
    }
}