# codeforces-tools
An advanced tool to give you a fruitful experience with codeforces :rocket: :heart:

#pre-processing data
In this part, The data files are read ,processed , serialized and written on the disk with the data needed for subsequent problems.
There are Three Pair types that were used:
1- Pair with the user handle and that rating of user
2- Pair with the problem and it submission time.
3- Pair with the problem and the problem's first submission.

In the solver class these methods are responsible for pre processing the data : `prepareProblems()`,`loadUsers()` and `processContestRangking()`.
For each user , a user have  hashmap <Integer,Integer> which correponds a contest id to the ranking of the user in this contest.
For each user the first submission for a problem that was in a contest is also saved in the desk to be used in problem 1.2

There is also another file that contains an array list of pairs, each pair is a user handle and the rating of user to be used in 1.3.
Another arraylist is saved which contains the accepted problems in the form of pairs where each pair contains the problem name and submission time.


The `prepareProblems()` is used to gather the date for problems and get the points for each problem and the tags for the problem


##problem 1.2


# problem 1.3

In the problem here , The solution is based on 2 arraylists coming from the pre processing of data as one is the ArrayList of
pairs of the problem and the time of the problem solved for each user, this list only includes accepted problems. The other arrayList contatins
also a pair but the pair here is the user and the user's rating. We use the second arraylist and binary search for the start of the range
and after finding the start rating all users after it are added to the list of users we are currently interested in.

after that for each user we binary search for the start of the time interval in the arraylist of problem,SubmissionTime Pair and after that
the number of problems in range is counted.
Here segment tree could have been a better option to get the count in lg(n) time However a new segment tree would have been constructed for each user.

After sorting the users based on activity the requested number of users is returned as a result.
The total time should be ~Nlg(N)~.

##problem 1.4