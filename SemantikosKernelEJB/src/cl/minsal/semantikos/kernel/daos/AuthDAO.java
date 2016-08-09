package cl.minsal.semantikos.kernel.daos;

import cl.minsal.semantikos.kernel.util.ConnectionBD;
import cl.minsal.semantikos.kernel.util.StringUtils;
import cl.minsal.semantikos.model.Category;
import cl.minsal.semantikos.model.Profile;
import cl.minsal.semantikos.model.User;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by des01c7 on 01-07-16.
 */

@Stateless
public class AuthDAO {


    @PersistenceContext(unitName = "SEMANTIKOS_PU")
    EntityManager em;

    public User getUserById(long id) {

        Query q = em.createNativeQuery("Select * from semantikos.smtk_user u where u.id_user = ? ");

        q.setParameter(1,id);


        Object sresult = q.getSingleResult();

        if (sresult ==null)
            return null;

        User user = makeUserFromResult((Object[]) sresult);

        user.setProfiles(getUserProfiles(id));

        return user;
    }


    public List<Profile> getUserProfiles(Long userId){

        Query q = em.createNativeQuery("Select p.* from semantikos.smtk_profiles p, semantikos.smtk_user_profile up where up.id_user = ? and up.id_profile = p.id_profile ");

        q.setParameter(1,userId);

        List<Profile> profiles = new ArrayList<Profile>();

        for (Object row : q.getResultList()) {
            Profile profile = makeProfileFromResult((Object[]) q.getSingleResult());
            profiles.add(profile);
        }


        return profiles;

    }

    private Profile makeProfileFromResult(Object[] row) {
        Profile p = new Profile();

        p.setIdProfile(((BigInteger)row[0]).longValue() );
        p.setName((String)row[1]);
        p.setDescription((String)row[2]);

        return p;
    }


    private User makeUserFromResult(Object[] row) {

        User u = new User();

        u.setIdUser(((BigInteger)row[0]).longValue() );
        u.setUsername((String)row[1]);
        u.setPasswordHash((String)row[2]);
        u.setPasswordSalt((String)row[3]);
        u.setName((String)row[4]);
        u.setLastName((String)row[5]);
        u.setSecondLastName((String)row[6]);
        u.setEmail((String)row[7]);

        u.setLocked((Boolean) row[8]);
        u.setFailedLoginAttempts((Integer)row[9]);

        u.setLastLogin((Timestamp)row[10]);
        u.setLastPasswordChange((Timestamp)row[11]);

        u.setLastPasswordHash1((String)row[12]);
        u.setLastPasswordHash2((String)row[13]);
        u.setLastPasswordHash3((String)row[14]);
        u.setLastPasswordHash4((String)row[15]);

        u.setLastPasswordSalt1((String)row[16]);
        u.setLastPasswordSalt2((String)row[17]);
        u.setLastPasswordSalt3((String)row[18]);
        u.setLastPasswordSalt4((String)row[19]);

        u.setRut((String)row[20]);

        return u;
    }



    public List<User> getAllUsers() {

        ArrayList<User> users = new ArrayList<>();

        Query q = em.createNativeQuery("Select * from semantikos.smtk_user");

        for (Object row : q.getResultList()) {
            User user = makeUserFromResult((Object[]) row);
            users.add(user);
        }



        return users;

    }

    public void createUser(User user) throws UserExistsException{

        Query q = em.createNativeQuery("INSERT INTO semantikos.smtk_user (id_user, username, password_hash, password_salt, name, last_name, second_last_name, email, locked, failed_login_attempts, last_login, last_password_change, last_password_hash1, last_password_hash2, last_password_hash3, last_password_hash4, last_password_salt1, last_password_salt2, last_password_salt3, last_password_salt4, rut) " +
                "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, DEFAULT, DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        q.executeUpdate();

    }


    public void updateUser(User user) {

        Query q = em.createNativeQuery("UPDATE semantikos.smtk_user " +
                "set name = ?, " +
                "last_name = ?, " +
                "second_last_name = ?, " +
                "email = ?, " +
                "rut= ?  " +
                "where id_user = ?");

        q.setParameter(1,user.getName());
        q.setParameter(2,user.getLastName());
        q.setParameter(3,user.getSecondLastName());
        q.setParameter(4,user.getEmail());
        q.setParameter(5,user.getRut());
        q.setParameter(6,user.getIdUser());

        q.executeUpdate();



    }

    public List<Profile> getAllProfiles() {
        ArrayList<Profile> profiles = new ArrayList<>();

        Query q = em.createNativeQuery("Select * from semantikos.smtk_profiles");

        for (Object row : q.getResultList()) {
            Profile profile = makeProfileFromResult((Object[]) row);
            profiles.add(profile);
        }



        return profiles;
    }


    private class UserExistsException extends Exception {
    }
}
