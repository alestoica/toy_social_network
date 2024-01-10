package org.example.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SocialNetwork {
    private long maxi = 1;
    private ArrayList<User> users;
    private int nrCommunities = 0;
    Map<Long, Integer> visited;
    public SocialNetwork(ArrayList<User> users) {
        this.users = users;
        this.visited = new HashMap<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public int getNrCommunities() {
        return nrCommunities;
    }

    @Override
    public String toString () {
        ArrayList<String> nameList = new ArrayList<>();

        if (users != null)
            users.forEach(user -> { nameList.add(user.getFirstName() + " " + user.getLastName()); });

        return "Social Network { users = " + nameList + "; nrCommunities = " + nrCommunities + "}";
    }

    /**
     * initializes the 'visited' map with default values for each user in the 'users' collection
     */
    public void initializeVisited() {
        users.forEach(user -> visited.put(user.getId(), 0));
    }

    /**
     * performs a DFS starting from the given user within the specified community
     *
     * @param user the user to start the DFS from
     * @param community the identifier for the community to which the 'user' belongs
     *
     * @throws IllegalArgumentException if 'user' is null or if 'community' is a negative integer
     */
    public void dfs(User user, int community) {
        visited.put(user.getId(), community);

        Predicate<User> isNotVisitedAndIsFriend = u -> visited.get(u.getId()) == 0 && u.isFriend(user);

        users.forEach(u -> {
            if(isNotVisitedAndIsFriend.test(u)){
                dfs(u, community);
                visited.put(u.getId(), community);
            }
        });
    }

    /**
     * calculates the number of communities using DFS on a collection of users
     *
     * @return the number of distinct communities
     */
    public Integer nrCommunities() {
        initializeVisited();

        nrCommunities = 0;

        Predicate<User> isNotVisited = u -> visited.get(u.getId()) == 0;

        users.forEach(user -> {
            if(isNotVisited.test(user)){
                dfs(user, nrCommunities + 1);
                nrCommunities++;
            }
        });

        return nrCommunities;
    }

    /**
     * returns an ArrayList of User objects representing the users that belong to the most sociable community
     *
     * @return an ArrayList of User objects representing the most sociable users
     */
    public ArrayList<User> mostSociable() {
        nrCommunities = nrCommunities();
        int idCommunity = 0;

        for (int i = 1; i <= nrCommunities; i++) {
            int finalI = i;
            Predicate<User> isFromCommunity = user -> visited.get(user.getId()) == finalI;

            long count = users.stream()
                    .filter(isFromCommunity)
                    .count();

            if(count > maxi) {
                maxi = count;
                idCommunity = i;
            }
        }

        int finalIdCommunity = idCommunity;
        Predicate<User> isFromCommunity = user -> visited.get(user.getId()) == finalIdCommunity;

        return (ArrayList<User>)users.stream()
                .filter(isFromCommunity)
                .collect(Collectors.toList());
    }
}
