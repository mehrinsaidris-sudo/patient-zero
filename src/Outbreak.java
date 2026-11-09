import java.util.*;

/**
 * Mission 5. Dynamic programming on the tree of infections. Root the tree in person 0.
 * Input.Tree.neighbours() gives the neighbours of every person. The number of people is at most about 100, so a recursive DFS is fine here.
 */
public class Outbreak {

    /**
     * Choose people to test so that for every link (somebody infected somebody) at least one of the two is tested,
     * and the total cost of testing is as small as possible. cost[v] is the price of testing person v.
     * Returns tested[v] = true if person v is tested.
     *
     * State for every person v (looking only at v and everything below it): the smallest cost when v IS tested, and
     * the smallest cost when v is NOT tested. What must then be true about the children of v in each case?
     * Do not forget to rebuild the answer (who exactly is tested), not only the cost: the picture needs it.
     * Main calls this twice: with all costs equal to 1 (the line "Tests needed") and with the real costs ("Cheapest testing").
     */
    public static boolean[] cheapestTests(int n, int[][] nb, int[] cost) {
        boolean[] tested = new boolean[n];

        if (n == 0) {
            return tested;
        }

        int[][] dp = new int[n][2];

        calculateCosts(0, -1, nb, cost, dp);

        boolean rootTested = dp[0][1] <= dp[0][0];

        rebuildTests(0, -1, rootTested, nb, dp, tested);

        return tested;
    }
    private static void calculateCosts(
            int v, int parent, int[][] nb,
            int[] cost, int[][] dp) {

        dp[v][0] = 0;
        dp[v][1] = cost[v];

        for (int child : nb[v]) {
            if (child == parent) {
                continue;
            }

            calculateCosts(child, v, nb, cost, dp);

            dp[v][0] += dp[child][1];

            dp[v][1] += Math.min(
                    dp[child][0],
                    dp[child][1]
            );
        }
    }
    private static void rebuildTests(
            int v, int parent, boolean isTested,
            int[][] nb, int[][] dp, boolean[] tested) {

        tested[v] = isTested;

        for (int child : nb[v]) {
            if (child == parent) {
                continue;
            }

            boolean childTested;

            if (!isTested) {
                childTested = true;
            } else {
                childTested = dp[child][1] <= dp[child][0];
            }

            rebuildTests(
                    child, v, childTested,
                    nb, dp, tested
            );
        }
    }

    /**
     * The number of links on the longest chain of infections, that is the longest path in the tree (counted in links).
     * A path goes up from one person and down to another; for every person you need the two longest ways down.
     * Example: a line 0-1-2-3 has chain 3; a star with centre 0 and leaves 1, 2, 3 has chain 2.
     */
    public static int longestChain(int n, int[][] nb) {
        if (n == 0) {
            return 0;
        }

        int[] longest = new int[1];

        findDepth(0, -1, nb, longest);

        return longest[0];
    }
    private static int findDepth(
            int v, int parent, int[][] nb, int[] longest) {

        int first = 0;
        int second = 0;

        for (int child : nb[v]) {
            if (child == parent) {
                continue;
            }

            int depth = findDepth(child, v, nb, longest) + 1;

            if (depth > first) {
                second = first;
                first = depth;
            } else if (depth > second) {
                second = depth;
            }
        }

        longest[0] = Math.max(
                longest[0],
                first + second
        );

        return first;
    }
}
