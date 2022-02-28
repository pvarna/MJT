package bg.sofia.uni.fmi.mjt.twitch.user;

import java.util.Objects;

public class UserClass implements User {
    private final String name;
    private UserStatus status;

    public UserClass(String name) {
        this.name = name;
        this.status = UserStatus.OFFLINE;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UserStatus getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserClass userClass = (UserClass) o;
        return name.equals(userClass.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
