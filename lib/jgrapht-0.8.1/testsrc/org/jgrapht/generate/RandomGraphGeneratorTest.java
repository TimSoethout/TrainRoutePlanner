/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* -----------------
 * RandomGraphGeneratorTest.java
 * -----------------
 * (C) Copyright 2005-2008, by Assaf Lehr and Contributors.
 *
 * Original Author:  Assaf Lehr
 * Contributor(s):   -
 *
 * $Id: RandomGraphGeneratorTest.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 */
package org.jgrapht.generate;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.experimental.isomorphism.*;
import org.jgrapht.graph.*;


/**
 * @author Assaf
 * @since Aug 6, 2005
 */
public class RandomGraphGeneratorTest
    extends TestCase
{
    //~ Methods ----------------------------------------------------------------

    public void testGenerateDirectedGraph()
    {
        List<Graph<Integer, DefaultEdge>> graphArray =
            new ArrayList<Graph<Integer, DefaultEdge>>();
        for (int i = 0; i < 3; ++i) {
            graphArray.add(
                new SimpleDirectedGraph<Integer, DefaultEdge>(
                    DefaultEdge.class));
        }

        generateGraphs(graphArray, 11, 100);

        assertTrue(
            EdgeTopologyCompare.compare(graphArray.get(0), graphArray.get(1)));
        // cannot assert false , cause it may be true once in a while (random)
        // but it generally should work.
        // assertFalse(EdgeTopologyCompare.compare(graphArray.get(1),graphArray.get(2)));
    }

    public void testGenerateListenableUndirectedGraph()
    {
        List<Graph<Integer, DefaultEdge>> graphArray =
            new ArrayList<Graph<Integer, DefaultEdge>>();
        for (int i = 0; i < 3; ++i) {
            graphArray.add(
                new ListenableUndirectedGraph<Integer, DefaultEdge>(
                    DefaultEdge.class));
        }

        generateGraphs(graphArray, 11, 50);

        assertTrue(
            EdgeTopologyCompare.compare(graphArray.get(0), graphArray.get(1)));
    }

    /**
     * Generates 3 graphs with the same numOfVertex and numOfEdges. The first
     * two are generated using the same RandomGraphGenerator; the third is
     * generated using a new instance.
     *
     * @param graphs array of graphs to generate
     * @param numOfVertex number of vertices to generate per graph
     * @param numOfEdges number of edges to generate per graph
     */
    private static void generateGraphs(
        List<Graph<Integer, DefaultEdge>> graphs,
        int numOfVertex,
        int numOfEdges)
    {
        RandomGraphGenerator<Integer, DefaultEdge> randomGen =
            new RandomGraphGenerator<Integer, DefaultEdge>(
                numOfVertex,
                numOfEdges);

        randomGen.generateGraph(
            graphs.get(0),
            new IntegerVertexFactory(),
            null);

        // use the same randomGen
        randomGen.generateGraph(
            graphs.get(1),
            new IntegerVertexFactory(),
            null);

        // use new randomGen here
        RandomGraphGenerator<Integer, DefaultEdge> newRandomGen =
            new RandomGraphGenerator<Integer, DefaultEdge>(
                numOfVertex,
                numOfEdges);

        newRandomGen.generateGraph(
            graphs.get(2),
            new IntegerVertexFactory(),
            null);
    }
}

// End RandomGraphGeneratorTest.java
