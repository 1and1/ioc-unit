package com.oneandone.iocunit.analyzer.algorithms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.Phase3Fixer;

public class Phase3UnambiguerTest {

    Phase3Fixer phase3Fixer = new Phase3Fixer(null);

    private List<List<Integer>> listOf(int[][] input) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] ia: input) {
            ArrayList<Integer> al = new ArrayList<>(ia.length);
            for (int i: ia)
                al.add(i);
            result.add(al);
        }
        return result;
    }

    private void exec(int[][] input, int[] result) {
        Set<Integer> tmp = phase3Fixer.optimizeUsage(listOf(input));
        assertEquals(tmp.size(), result.length);
        for (int i = 0; i < result.length; i++) {
            int l = result[i];
            assertTrue(tmp.contains(l));
        }
    }

    @Test
    public void greenpath() {
        exec(new int[][] {}, new int[] {});
        exec(new int[][] {{1}}, new int[] {1});
        exec(new int[][] {{1,2}}, new int[] {1});
        exec(new int[][] {{1,2,3}}, new int[] {1});
        exec(new int[][] {{1}, {1}}, new int[] {1});
        exec(new int[][] {{1,2}, {1}}, new int[] {1});
        exec(new int[][] {{2}, {1}}, new int[] {1,2});
        exec(new int[][] {{1,2}, {1,2}}, new int[] {1});
        exec(new int[][] {{1,2}, {2,1}}, new int[] {1});
        exec(new int[][] {{2,1}, {1,2}}, new int[] {2});
        exec(new int[][] {{1,3,4,5}, {1,2,3,4,5}}, new int[] {1});
        exec(new int[][] {{3,4,5}, {1,2,3,4,5}}, new int[] {3});
        exec(new int[][] {{3,4,5}, {1,2,3,4,5},{5}}, new int[] {5});
        exec(new int[][] {{3,4,5}, {1,2,3,4,5},{2,5,3,4}}, new int[] {3});
        exec(new int[][] {{3,4,5}, {1,2,3,4,5},{2,5,3,4},{4}}, new int[] {4});
        exec(new int[][] {{0,6}, {3,4,5}, {1,2,3,4,5}, {2,5,3,4}, {4}}, new int[] {0,4});
        exec(new int[][] {{1,2}, {1,2}, {2}}, new int[] {2});
        exec(new int[][] {{1}, {1,2},{3}}, new int[] {1,3});
        exec(new int[][] {{1}, {1,2},{2}}, new int[] {});
    }

    @Test
    public void test() {

    }

}
