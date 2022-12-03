package ru.sejapoe.digitalhotelserver.user;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.sejapoe.digitalhotelserver.core.security.BitArray256;
import ru.sejapoe.digitalhotelserver.user.session.Session;
import ru.sejapoe.digitalhotelserver.user.session.SessionRepository;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.sejapoe.digitalhotelserver.core.security.Auth.*;

@Service
public class UserService {
    private final Map<String, BitArray256> activeRegistrations = new HashMap<>();
    private final Map<String, Pair<User, BitArray256>> activeLogins = new HashMap<>();
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public UserService(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    public BitArray256 startRegistration(Pair<String, String> clientRegister) throws UserAlreadyExists {
        String login = clientRegister.getFirst();

        if (userRepository.existsById(login)) throw new UserAlreadyExists();

        BitArray256 saltC = BitArray256.fromHexString(clientRegister.getSecond());
        BitArray256 saltS = random256();
        BitArray256 salt = xorByteArrays(saltS, saltC);

        activeRegistrations.put(login, salt);

        return saltS;
    }

    public void finishRegistration(Pair<String, BigInteger> clientRegister) {
        String login = clientRegister.getFirst();
        BitArray256 salt = activeRegistrations.get(login);

        userRepository.save(
                new User(login, salt, clientRegister.getSecond())
        );
        activeRegistrations.remove(login);
    }

    public Pair<String, BigInteger> login(Pair<String, BigInteger> clientLogin) throws IOException, NoSuchAlgorithmException, NoSuchUser {
        Optional<User> userOptional = userRepository.findById(clientLogin.getFirst());
        if (userOptional.isEmpty()) throw new NoSuchUser();

        User user = userOptional.get();

        BitArray256 b = random256();
        BigInteger B = k.multiply(user.getVerifier()).add(modPow(b));

        BitArray256 u = hash(concatByteArrays(clientLogin.getSecond().toByteArray(), B.toByteArray()));


        BigInteger S = clientLogin.getSecond().multiply(user.getVerifier().modPow(u.asBigInteger(), N)).modPow(b.asBigInteger(), N);
        BitArray256 sessionKey = hash(S.toByteArray());

        BitArray256 M = hash(
                concatByteArrays(
                        xorByteArrays(BitArray256.fromBigInteger(N), BitArray256.fromBigInteger(g)).asByteArray(),
                        user.getUsername().getBytes(StandardCharsets.UTF_8),
                        user.getSalt().asByteArray(),
                        clientLogin.getSecond().toByteArray(),
                        B.toByteArray(),
                        sessionKey.asByteArray()
                ));

        activeLogins.put(M.asHexString(), Pair.of(user, sessionKey));
        return Pair.of(user.getSalt().asHexString(), B);
    }

    public void confirm(String data) throws WrongPasswordException {
        Pair<User, BitArray256> pair = activeLogins.get(data);
        if (pair == null) throw new WrongPasswordException();
        sessionRepository.save(new Session(pair.getFirst(), pair.getSecond()));
    }

    public static class UserAlreadyExists extends Exception {
    }

    public static class NoSuchUser extends Exception {
    }

    public static class WrongPasswordException extends Exception {
    }
}
// 6fffffff9fffffffacffffffe2fffffffdbffffffbeffffff946fffffffb149ffffffa71ffffffd2ffffffd63c651f34426dffffffd46a7141ffffffe8fffffff9ffffff80fffffff523ffffffceffffffa1
// 6fffffff9fffffffacffffffe2fffffffdbffffffbeffffff946fffffffb149ffffffa71ffffffd2ffffffd63c651f34426dffffffd46a7141ffffffe8fffffff9ffffff80fffffff523ffffffceffffffa1