# codeforces-tools
An advanced tool to give you a fruitful experience with codeforces :rocket: :heart:

# pre-processing data

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


## problem 1.2

Documentation on the code will do better understanding the concept
We use contestId + problemIndex as identifier for a problem
We process in the `Contest` class and save most of the data of it
We also serialize two hashMaps in the user one is `firstSubmission` which is relative time in the contest for this submission
We also serialize in prepare problems the other one is `problemsToPoints` to map problem to corresponding points
Actual points / penalties are computed in the preparation process (initialistion of a contest)
We use compute users new rating by going through the contests he entered which are saved in `contestRanking` for each user
Then we compute the new points / penalty based on the problems solved of this contest

## problem 1.3

In the problem here , The solution is based on 2 arraylists coming from the pre processing of data as one is the ArrayList of
pairs of the problem and the time of the problem solved for each user, this list only includes accepted problems. The other arrayList contatins
also a pair but the pair here is the user and the user's rating. We use the second arraylist and binary search for the start of the range
and after finding the start rating all users after it are added to the list of users we are currently interested in.

after that for each user we binary search for the start of the time interval in the arraylist of problem,SubmissionTime Pair and after that
the number of problems in range is counted.
Here segment tree could have been a better option to get the count in lg(n) time However a new segment tree would have been constructed for each user.

After sorting the users based on activity the requested number of users is returned as a result.
The total time should be `Nlg(N)`.

## problem 1.4

This problem is solved in the class ProblemSelection, first the data is loaded from the disk through the method prepareProblem() which takes O(1) loading the required data, then using problems are filtered in O(log n) to get problems in the range of [minSolved, maxSolved] then the result from these filtered are re-filtered with tags in O(n*log n) leaving the desired problems to deal with later

After that Users with the handles are loaded from the disk and relations between which problem is solved before the other is calculated in O(n*m^2) where n is number of users and m number of problems.

Finally we build our adjaceny list, check if a problem x was solved by p% number of user than y an edge is added to the list. Using tarjanSCC to build Strongly Component and then going through these components to remove all the deadlock sets and represent them with a single node, Finally we do topological sort on our new graph and return the result added to it the deadlocks in between