# codeforces-tools
An advanced tool to give you a fruitful experience with codeforces :rocket: :heart:

#pre-processing data

#problem 1.2

# problem 1.3

In the problem here , The solution is based on 2 arraylists coming from the pre processing of data as one is the ArrayList of
pairs of the problem and the time of the problem solved for each user, this list only includes accepted problems. The other arrayList contatins
also a pair but the pair here is the user and the user's rating. We use the second arraylist and binary search for the start of the range
and after finding the start rating all users after it are added to the list of users we are currently interested in.

after that for each user we binary search for the start of the time interval in the arraylist of problem,SubmissionTime Pair and after that
the number of problems in range is counted.
Here segment tree could have been a better option to get the count in lg(n) time However a new segment tree would have been constructed for each user.

After sorting the users based on activity the requested number of users is returned as a result.