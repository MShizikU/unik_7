package org.example;

import io.reactivex.Observable;
//SSidorov
class UserFriend {
    private final int userId;
    private final int friendId;
    public UserFriend(int userId, int friendId) {
        this.userId = userId;
        this.friendId = friendId;
    }
    public int getUserId() {
        return userId;
    }
    public int getFriendId() {
        return friendId;
    }
    public static Observable<UserFriend> getFriends(int userId) {
        // Здесь вы можете выполнить логику для получения друзей пользователя по userId.
        // В этом примере, мы будем возвращать случайные объекты UserFriend.
        return Observable.fromArray(
                new UserFriend(userId, (int) (Math.random() * 100)),
                new UserFriend(userId, (int) (Math.random() * 100)),
                new UserFriend(userId, (int) (Math.random() * 100))
        );
    }
    public static void main(String[] args) {
        // Создаем массив случайных userId
        Integer[] userIdArray = {1, 2, 3, 4, 5};
        // Создаем поток userId из массива
        Observable<Integer> userIdStream = Observable.fromArray(userIdArray);
        // Преобразуем поток userId в поток объектов UserFriend
        Observable<UserFriend> userFriendStream = userIdStream
                .flatMap(userId -> getFriends(userId)); // Используем функцию getFriends
        // Подписываемся на поток и выводим результат
        userFriendStream.subscribe(userFriend -> {
            System.out.println("User: " + userFriend.getUserId() + ", Friend: "
                    + userFriend.getFriendId());
        });
    }
}
