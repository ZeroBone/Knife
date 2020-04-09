package net.zerobone.knife.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class MatrixOrientedGraph {

    private final int vertices;

    private final byte[] matrix;

    public MatrixOrientedGraph(int vertices) {
        this.vertices = vertices;
        matrix = new byte[vertices * vertices];
    }

    public void addEdge(int start, int dest) {
        assert start < vertices;
        assert dest < vertices;
        matrix[start * vertices + dest] = 1;
    }

    public void transitiveClosure() {

        for (int start = 0; start < vertices; start++) {

            for (Iterator<Integer> it = bfs(start); it.hasNext();) {

                int reachedNode = it.next();

                addEdge(start, reachedNode);

            }

        }

    }

    public Iterator<Integer> bfs(int start) {

        assert start < vertices;

        boolean[] visited = new boolean[vertices];

        visited[start] = true;

        final Queue<Integer> queue = new LinkedList<>();

        queue.add(start);

        return new Iterator<Integer>() {

            @Override
            public boolean hasNext() {
                return !queue.isEmpty();
            }

            @Override
            public Integer next() {

                assert !queue.isEmpty();

                int vertex = queue.poll();

                for (int v = 0; v < vertices; v++) {

                    if (matrix[vertex * vertices + v] == 0) {
                        continue;
                    }

                    if (visited[v]) {
                        continue;
                    }

                    queue.add(v);

                    visited[v] = true;

                }

                return vertex;

            }

        };

    }

    public int outcomingDegree(int vertex) {

        assert vertex < vertices;

        int degree = 0;

        for (int i = 0; i < vertices; i++) {

            if (matrix[vertex * vertices + i] == 1) {
                degree++;
            }

        }

        return degree;

    }

}