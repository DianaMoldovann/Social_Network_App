package com.example.final_social_network.UI;


import com.example.final_social_network.Domain.Friendship;
import com.example.final_social_network.Domain.Tuple;
import com.example.final_social_network.Domain.User;
import com.example.final_social_network.Service.FriendshipService;
import com.example.final_social_network.Service.UserService;
import com.example.final_social_network.Validator.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UI {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final Scanner scanner = new Scanner(System.in);

    private final Consumer<String> write = System.out::print;
    private final Consumer<String> writeln = System.out::println;

    private final Supplier<Long> readID = () -> {
        while (true) {
            try {
                write.accept("Please enter the ID : ");
                Long id = scanner.nextBigInteger().longValue();
                scanner.nextLine();
                return id;
            } catch (InputMismatchException e) {
                writeln.accept("The ID is invalid, please enter an integer");
                scanner.nextLine();
            }
        }
    };


    private final Supplier<LocalDate> readDate = () -> {
        while (true) {
            try {
                System.out.print("Enter the date (format: yyyy-MM-dd): ");
                String inputDate = scanner.next();

                // Converteste String-ul la LocalDate
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDate.parse(inputDate, formatter);
            }
            catch (DateTimeParseException e) {
                System.out.println("The date entered is not valid" + e.getMessage());
                scanner.nextLine();
            }
        }
    };


    public UI(UserService userServicee, FriendshipService friendshipServicee) {
        this.userService = userServicee;
        this.friendshipService = friendshipServicee;
    }


    private  <T> void printEntities(Iterable<T> entities) {
        entities.forEach(x -> writeln.accept(x.toString()));
//        for(T e: entities) {
//            writeln.accept(e.toString());
//        }
    }


    private void addUserUI() {
        write.accept("Please enter the user's firstname :");
        String firstname = scanner.nextLine();
        write.accept("Please enter the user's lastname :");
        String lastname = scanner.nextLine();
        write.accept("Please enter the user's email :");
        String email = scanner.nextLine();
        write.accept("Please enter the user's password :");
        String password = scanner.nextLine();
        userService.add(firstname, lastname, email, password);
        writeln.accept("The user has been successfully added.");
    }

    private void deleteUserUI() {
        long id = readID.get();
        User u = userService.delete(id);
        writeln.accept( u + "has been successfully deleted.");
    }


    private void findUserUI() {
        long id = readID.get();
        User u = userService.findOne(id);
        writeln.accept("the searched user is: " + u);
    }


    private void updateUserUI() {
        long id = readID.get();
        write.accept("Please enter the user's firstname :");
        String firstname = scanner.nextLine();
        write.accept("Please enter the user's lastname :");
        String lastname = scanner.nextLine();
        write.accept("Please enter the user's email :");
        String email = scanner.nextLine();
        write.accept("Please enter the user's password :");
        String password = scanner.nextLine();
        userService.update(id, firstname, lastname, email, password);
        writeln.accept("The user has been successfully updated.");
    }


    private void printUsersUI() {
        printEntities(userService.getAll());
    }



    private void addFriendshipUI() {
        writeln.accept("Please enter the ID of the first user");
        long id1 = readID.get();
        writeln.accept("Please enter the ID of the second user");
        long id2 = readID.get();
        friendshipService.add(id1, id2);
        writeln.accept("Users became friends successfully.");
    }

    private void deleteFriendshipUI() {
        writeln.accept("Please enter the ID of the first user");
        long id1 = readID.get();
        writeln.accept("Please enter the ID of the second user");
        long id2 = readID.get();
        friendshipService.delete(id1, id2);
        writeln.accept("Users are not friends anymore" );
    }


    private void findFriendshipUI() {
        writeln.accept("Please enter the ID of the first user");
        Long id1 = readID.get();
        writeln.accept("Please enter the ID of the second user");
        Long id2 = readID.get();
        Friendship friendship = friendshipService.findOne(new Tuple<>(id1, id2));
        writeln.accept("The friendship you are looking for is " + friendship);
    }


    private void updateFriendshipUI() {
        writeln.accept("Please enter the ID of the first user");
        long id1 = readID.get();
        writeln.accept("Please enter the ID of the second user");
        long id2 = readID.get();
        write.accept("Please enter the new date of the friendship: ");
        LocalDate date = readDate.get();
        friendshipService.update(id1, id2, date);
        writeln.accept("The date has been successfully updated.");
    }


    private void printFriendshipsUI() {
        printEntities(friendshipService.getAll());
    }

    private void numberOfCuminitiesUI() {
        int nr = friendshipService.numberOfComunities();
        writeln.accept("Number of comuniities is " + nr);
    }


    private void theMostSociableComunity() {
        ArrayList<Long> theMostSociable = (ArrayList<Long>) friendshipService.findTheMostSociableComunity();
        writeln.accept("The most sociable comunity: ");
        for (Long id :theMostSociable) {
            writeln.accept(id.toString());
        }
    }

    private void allFriendshipsFromAMonth() {
        write.accept("Please enter the user id: ");
        Long id = readID.get();
        write.accept("Please enter a month (mm): ");
        int m = scanner.nextInt();
        write.accept("Please enter a year (yyyy): ");
        int y = scanner.nextInt();
        ArrayList<Friendship> friendships = (ArrayList<Friendship>) friendshipService.allFriendshipsFromAMonth(id, m, y);
        if (friendships.isEmpty()) {
            writeln.accept("Not friendships in the specified month!");
        }
        else {
            writeln.accept( "Friendships from the specified month are");
            printEntities(friendships);
        }
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        //defaultData();

        Map<String, Consumer<UI>> menuOptions = new HashMap<>();
        menuOptions.put("1", UI::addUserUI);
        menuOptions.put("2", UI::deleteUserUI);
        menuOptions.put("3", UI::findUserUI);
        menuOptions.put("4", UI::updateUserUI);
        menuOptions.put("5", UI::printUsersUI);
        menuOptions.put("6", UI::addFriendshipUI);
        menuOptions.put("7", UI::deleteFriendshipUI);
        menuOptions.put("8", UI::findFriendshipUI);
        menuOptions.put("9", UI::updateFriendshipUI);
        menuOptions.put("10", UI::printFriendshipsUI);
        menuOptions.put("11", UI::numberOfCuminitiesUI);
        menuOptions.put("12", UI::theMostSociableComunity);
        menuOptions.put("13", UI::allFriendshipsFromAMonth);
        menuOptions.put("14", ui -> System.out.println("Ieșire"));

        writeln.accept(" MENU:");
        writeln.accept("1. Add user");
        writeln.accept("2. Delete user");
        writeln.accept("3. Find user");
        writeln.accept("4. Update user");
        writeln.accept("5. Print all users");
        writeln.accept("6. Add friendship");
        writeln.accept("7. Delete friendship");
        writeln.accept("8. Find friendship");
        writeln.accept("9. Update friendship");
        writeln.accept("10. Prints all friendships");
        writeln.accept("11. Number of comunities");
        writeln.accept("12. The most sociable comunity");
        writeln.accept("13. All friendships from a specific month");
        writeln.accept("14. Ieșire");

        //erori daca adaugi acelasi id


        while (!exit) {
            System.out.print("Choose an option: ");
            String option = scanner.nextLine();

            if (menuOptions.containsKey(option)) {
                try {
                    menuOptions.get(option).accept(this);
                }
                catch (ValidationException | IllegalArgumentException ve) {
                    System.out.println(ve.getMessage());
                }
            }
            else System.out.println("Optiune invalida! ");
            if ( option.equals("14" )) { exit = true;}
        }
    }

}