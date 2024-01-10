package com.example.final_social_network.Service;


import com.example.final_social_network.Domain.FriendDTO;
import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Repository.FriendshipRepository.FriendshipDBRepository;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Page;
import com.example.final_social_network.Repository.FriendshipRepository.paging.Pageable;
import com.example.final_social_network.Repository.UserRepository.UserDBRepository;
import com.example.final_social_network.Validator.Validator;
import com.example.final_social_network.utils.events.ChangeEventType;
import com.example.final_social_network.utils.events.Event;
import com.example.final_social_network.utils.events.FriendshipChangeEvent;
import com.example.final_social_network.utils.observer.Observable;
import com.example.final_social_network.utils.observer.Observer;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class FriendshipService implements Observable<Event> {
    private final FriendshipDBRepository friendshipRepo;
    //private final Repository<Long, User> userRepo;
    private final UserDBRepository userRepo;
    private final Validator<Friendship> val;
    private final List<Observer<Event>> observers = new ArrayList<>();


    public FriendshipService(FriendshipDBRepository friendshipRepo, UserDBRepository userRepo, Validator<Friendship> val) {
        this.friendshipRepo = friendshipRepo;
        this.userRepo = userRepo;
        this.val = val;
    }


    public Friendship findOne(Tuple<Long, Long> id ) {
        Optional<Friendship> fr_op = friendshipRepo.findOne(id);
        if (fr_op.isEmpty())
            throw new IllegalArgumentException("The friendship you are looking for doesn't exist");
        else
            return fr_op.get();
    }


    public Iterable<Friendship> getAll() {
        return friendshipRepo.getAll();
    }


    public void add(Long idUser1, Long idUser2){
        if (userRepo.findOne(idUser1).isEmpty() || userRepo.findOne(idUser2).isEmpty())
            throw new IllegalArgumentException("User ids must be existing and valid");
        Friendship friendship = new Friendship(new Tuple<>(idUser1, idUser2));
        val.validate(friendship);
        Optional<Friendship> fr_op = friendshipRepo.add(friendship);
        if (fr_op.isPresent())
            throw new IllegalArgumentException("Users are already friends\n" );
        else {
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.ADD, null, null));
        }
    }


    public void delete(Long idUser1, Long idUser2){
        Optional<Friendship> fr_op = friendshipRepo.findOne(new Tuple<>(idUser1, idUser2));
        friendshipRepo.delete(new Tuple<>(idUser1, idUser2));
        if (fr_op.isEmpty())
            throw new IllegalArgumentException("The friendship you are looking for doesn't exist");
        else {
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.DELETE, fr_op.get(), null));
        }
    }


    public void update(Long idUser1, Long idUser2, LocalDate date) {
        Friendship updatedFriendship = new Friendship(new Tuple<>(idUser1, idUser2), date);
        val.validate(updatedFriendship);
        Optional<Friendship> fr_op = friendshipRepo.update(updatedFriendship);
        if (fr_op.isEmpty())
            throw new IllegalArgumentException("There is no friendship between the entered IDs.");
        else {
            notifyObservers(new FriendshipChangeEvent(ChangeEventType.UPDATE, fr_op.get(), updatedFriendship));
        }
    }

    public Iterable<FriendDTO> findFriendsOfUser(Long idUser) {
        List<FriendDTO> friends = new ArrayList<>();

        Iterable<Long> idsOfFrriends = friendshipRepo.findFriends(idUser);
        idsOfFrriends.forEach(idFriend -> {
            Friendship friendship = friendshipRepo.findOne(new Tuple<>(idUser, idFriend)).get();
            User fr = userRepo.findOne(idFriend).get();
            FriendDTO friend = new FriendDTO(idFriend, fr.getFirstName(), fr.getLastName(), fr.getEmail(), friendship.getDate());
            friends.add(friend);
        });
        return friends;
    }

    public Page<FriendDTO> findFriendsOfUserOnPage(Pageable pageable, Long idUser) {
        Page<Friendship> friendshipsOnPage = friendshipRepo.findFriendsOnPage(pageable, idUser);
        List<Friendship> friendships = (List<Friendship>) friendshipsOnPage.getElementsOnPage();
        List<FriendDTO> friends = new ArrayList<>();

        friendships.forEach(fr -> {
            Long idFriend = Objects.equals(fr.getId().getLeft(), idUser) ? fr.getId().getRight() : fr.getId().getLeft();
            User friend = userRepo.findOne(idFriend).get();
            FriendDTO friendDTO = new FriendDTO(idFriend, friend.getFirstName(), friend.getLastName(), friend.getEmail(), fr.getDate());
            friends.add(friendDTO);
        });
return new Page<>(friends, friendshipsOnPage.getTotalNrOfElems());
    }

    public FriendDTO findFriend(Long idUser, String emailFriend) {
        Optional<User> friend_user = userRepo.findOne(emailFriend);
        if (friend_user.isPresent()) {
            Optional<Friendship> friendship = friendshipRepo.findOne(new Tuple<>(idUser, friend_user.get().getId()));
            if (friendship.isPresent()) {
                return new FriendDTO(friend_user.get().getId(), friend_user.get().getFirstName(), friend_user.get().getLastName(),
                        friend_user.get().getEmail(), friendship.get().getDate());
            }else {
                throw new IllegalArgumentException("You are not friend with " + emailFriend);
            }
        } else {
            throw new IllegalArgumentException("There no user with this email!");
        }
    }


    private void dfs(Long v, HashMap<Long, Boolean> visited) {
        Stack<Long> stack = new Stack<>();
        stack.push(v);

        while (!stack.isEmpty()) {
            Long node = stack.pop();//scoatem ultimul nod adaugat (id-ul user-ului)
            if (!visited.get(node)) {
                visited.put(node, true); // Marcam nodul ca vizitat
                friendshipRepo.findFriends(node).forEach(neighbor -> {
                    if (!visited.get(neighbor)) {
                    stack.push(neighbor); // Adăugăm vecinii nevizitați în stivă pentru a-i explora
                    }
                });
//                for (Long neighbor : friendshipRepo.findFriends(node)) {
//                    if (!visited.get(neighbor)) {
//                        stack.push(neighbor); // Adăugăm vecinii nevizitați în stivă pentru a-i explora
//                    }
//                }
            }
        }
    }


    public int numberOfComunities() {
        int countCommunities = 0;
        HashMap<Long, Boolean> visited = new HashMap<>();
        for (User user : userRepo.getAll()) {
            visited.put(user.getId(), false);
        }
        for (User user : userRepo.getAll()) {
            if (!visited.get(user.getId())) {
                dfs(user.getId(), visited);
                countCommunities++;
            }
        }
        return countCommunities;
    }


    public Iterable<Long> findLongestPathFromNode(Long source) {
        Map<Long, Integer> distances = new HashMap<>(); //map pt distante(id-nod si distanta de la nodul sursa
        //friendshipRepo.findFriends(source).forEach(neighbor -> distances.put(neighbor, -1));
        for (User node : userRepo.getAll()) {
            distances.put(node.getId(), -1);
        }
        distances.put(source, 0);
        Stack<Long> stack = new Stack<>();
        stack.push(source);

        while (!stack.isEmpty()) {
            Long node = stack.pop();
            friendshipRepo.findFriends(node).forEach(neighbor -> {
                if (distances.get(neighbor) == -1) {
                    distances.put(neighbor, distances.get(node) + 1);
                    stack.push(neighbor);
                }
            });
//            for (Long neighbor : friendshipRepo.findFriends(node)) {
//                if (distances.get(neighbor) == -1) {
//                    distances.put(neighbor, distances.get(node) + 1);
//                    stack.push(neighbor);
//                }
//            }
        }

        // Găsiți nodul cu distanța maximă
        Long maxDistanceNode = source;
        int maxDistance = 0;
        for (Map.Entry<Long, Integer> entry : distances.entrySet()) {
            if (entry.getValue() > maxDistance) {
                maxDistance = entry.getValue();
                maxDistanceNode = entry.getKey();
            }
        }

        // Reconstituiți cel mai lung drum
        List<Long> longestPath = new ArrayList<>();
        longestPath.add(maxDistanceNode);
        Long current = maxDistanceNode;
        while (distances.get(current) > 0) {
            //mergem din aproape in aproape inapoi pe nodurile cu distanta cu o unitate mai mica
            for (Long neighbor : friendshipRepo.findFriends(current)) {
                if ( distances.get(neighbor) == distances.get(current) - 1) {
                    //if (distances.containsKey(neighbor) && distances.get(neighbor) == distances.get(current) - 1) {
                    longestPath.add(neighbor);
                    current = neighbor; // trecem pe codul precedent
                    break;
                }
            }
        }
        return longestPath;
    }


    public Iterable<Long> findTheMostSociableComunity() {
        List<Long> longestPath = new ArrayList<>();

        // Inițializăm o variabilă pentru a urmări lungimea drumului maxim
        int maxLength = -1;

        // Iterăm peste fiecare nod din comunitrate și căutăm cel mai lung drum
        for (User user : userRepo.getAll()) {
            List<Long> path = (ArrayList<Long>)findLongestPathFromNode(user.getId());
            if (path.size() > maxLength) {
                maxLength = path.size();
                longestPath = path;
            }
        }

        return longestPath;
    }

    public List<Friendship> allFriendshipsFromAMonth(Long id, int m, int y) {
//        ArrayList<Friendship> friendships = new ArrayList<>();
//        for (Friendship friendship : friendshipRepo.getAll()) {
//            if (friendship.getDate().getMonthValue() == m &&
//                    friendship.getDate().getYear() == y) {
//                friendships.add(friendship);
//            }
//        }
        List<Friendship> filteredFriendships = (List<Friendship>) friendshipRepo.getAll();
        return filteredFriendships
                .stream()
                .filter(friendship ->
                        (Objects.equals(friendship.getId().getLeft(), id) || Objects.equals(friendship.getId().getRight(), id)) &&
                        friendship.getDate().getMonthValue() == m && friendship.getDate().getYear() == y)
                .collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<Event> e) {observers.add(e);}

    @Override
    public void removeObserver(Observer<Event> e) {observers.remove(e);}

    @Override
    public void notifyObservers(Event t) {observers.forEach(o -> o.update(t));}
}
