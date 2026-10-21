import java.util.*;

public class Dijkstras {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of vertices: ");
        int n = sc.nextInt();
        int[][] graph = new int[n][n];
        System.out.println("\nEnter the adjacency matrix:");
        System.out.println("(Enter 0 if there is no edge)");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                graph[i][j] = sc.nextInt();
            }
        }
        System.out.print("\nEnter source vertex (0 to " + (n - 1) + "): ");
        int source = sc.nextInt();
        int[] distance = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(distance, Integer.MAX_VALUE);
        distance[source] = 0;
        for (int count = 0; count < n - 1; count++) {
            int u = -1;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && distance[i] < min) {
                    min = distance[i];
                    u = i;
                }
            }
            if (u == -1)
                break;
            visited[u] = true;
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 &&
                        !visited[v] &&
                        distance[u] != Integer.MAX_VALUE &&
                        distance[u] + graph[u][v] < distance[v]) {
                    distance[v] = distance[u] + graph[u][v];
                }
            }
        }
        System.out.println("\nShortest distances from vertex " + source + ":");
        for (int i = 0; i < n; i++) {
            if (distance[i] == Integer.MAX_VALUE)
                System.out.println("To vertex " + i + " : No path");
            else
                System.out.println("To vertex " + i + " : " + distance[i]);
        }
        sc.close();
    }
}