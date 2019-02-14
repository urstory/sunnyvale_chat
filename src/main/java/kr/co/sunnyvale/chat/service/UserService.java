package kr.co.sunnyvale.chat.service;


import kr.co.sunnyvale.chat.domain.*;
import kr.co.sunnyvale.chat.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConnectionRepository userConnectionRepository;
    private final RoleRepository roleRepository;
    private final ExtraUserRepository extraUserRepository;

    @Transactional
    public User addUserConnection(Long userId, UserConnection userConnection){
        User user = userRepository.getUser(userId);
        user.addUserConnection(userConnection);
        return user;
    }

    @Transactional(readOnly = true)
    public User getUser(String email) {
        return userRepository.getUser(email);
    }

    @Transactional(readOnly = true)
    public User getUser(Long id) {
        return userRepository.getUser(id);
    }


    @Transactional(readOnly = true)
    public boolean isExistUser(UserConnection userConnection) {
        final User user = userRepository.getFindByUserConnections(userConnection);
        return (user != null);
    }

    @Transactional(readOnly = true)
    public User findBySocial(UserConnection userConnection) {
        final User user = userRepository.getFindByUserConnections(userConnection);
        if (user == null) throw new RuntimeException();
        return user;
    }

    @Transactional
    public User signUpSocial(UserConnection userConnection) {
        final User user = User.signUp(userConnection);
        user.addRole(getRole("TEMPUSER"));
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserConnection getUserConnection(ProviderType provider, String email) {
        return userConnectionRepository.getUserConnection(provider, email);
    }

    @Transactional(readOnly = true)
    public UserConnection getUserConnectionByProviderId(ProviderType provider, String prviderId) {
        return userConnectionRepository.getUserConnectionByProviderId(provider, prviderId);
    }


    @Transactional(readOnly = true)
    public Role getRole(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Transactional
    public User changeUserRole(Long userId, String loginUserEmail, String email, String name, String roleName) {
        User alreadyUser = userRepository.getUser(email);
        if(!loginUserEmail.equals(email) && alreadyUser != null)
            throw new RuntimeException("이미 존재하는 email입니다.");
        User user = userRepository.getUser(userId);
        user.setEmail(email);
        user.setName(name);
        user.setRoles(null); // 기존 롤들을 삭제한다.
        user.addRole(getRole(roleName));
        return user;
    }

    @Transactional
    public void deleteUserConnection(Long id, ProviderType providerType){
        userConnectionRepository.deleteUserConnection(id, providerType);
    }

    @Transactional(readOnly = true)
    public boolean hasUserConnection(Long id, ProviderType providerType){
        UserConnection userConnection = userConnectionRepository.getUserConnection(id, providerType);
        if(userConnection == null)
            return false;
        else
            return true;
    }

    @Transactional
    public User addUser(User user) {
        Role role = roleRepository.findRoleByName("EXTRAUSER");
        user.addRole(role);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByNickName(String nickName) {
        return userRepository.getUserByNickName(nickName);
    }

    @Transactional
    public void updateNickname(Long userId, String nickName) {
        User user = userRepository.getUser(userId);
        user.setNickName(nickName);
    }

    @Transactional
    public boolean changePassword(Long userId, String existingPassword, String password) {
        User user = userRepository.getUser(userId);
        if(user == null)
            return false;

        boolean updatePassword = false;
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // 사용자가 암호를 입력하지 않았고, DB에도 암호가 입력되지 않았을 경우
        if("".equals(existingPassword)
                &&
                (user.getPassword() == null ||  "".equals(user.getPassword()))
                ){
            updatePassword = true;
        }else {

            boolean matches = passwordEncoder.matches(existingPassword, user.getPassword());
            if (matches) { // 사용자가 입력한 암호와 일치할 경우
                updatePassword = true;
            }
        }

        if(updatePassword) {
            String encodePassword = passwordEncoder.encode(password);
            user.setPassword(encodePassword);
            return true;
        }else
            return false;

    }

    @Transactional
    public boolean changePassword(Long userId, String password) {
        User user = userRepository.getUser(userId);
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        return true;
    }

    @Transactional(readOnly = true)
    public UserConnection getUserConnection(Long userId, ProviderType providerType) {
        return userConnectionRepository.getUserConnectionByUserId(providerType, userId);
    }

    @Transactional
    public ExtraUser saveExtraUser(ExtraUser extraUser){
        ExtraUser extraUser1 = extraUserRepository.getExtraUserByUuid(extraUser.getUuid());
        if(extraUser1 != null)
            extraUserRepository.delete(extraUser1);
        return extraUserRepository.save(extraUser);
    }

    @Transactional(readOnly = true)
    public ExtraUser getExtraUser(String uuid){
        return extraUserRepository.getExtraUserByUuid(uuid);
    }

    @Transactional
    public void deleteExtraUser(String uuid){
        ExtraUser extraUser1 = extraUserRepository.getExtraUserByUuid(uuid);
        extraUserRepository.delete(extraUser1);
    }

    @Transactional
    public User changeUserRole(Long userId, String roleName) {
        User user = userRepository.getUser(userId);

        Set<Role> roles = user.getRoles();
        for(Role role: roles){
            if("EXTRAUSER".equals(role.getName()))
                roles.remove(role);
            if("TEMPUSER".equals(role.getName()))
                roles.remove(role);
        }
        user.addRole(getRole(roleName));
        return user;
    }
}