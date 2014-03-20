package KDTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import KDTree.KDTree.Axis;

public class KDSearcher {
	static Node[] KDSearch(Node target, Integer range, Node current, Integer depth, Stack<Node> nodes) {
		if (range == 0) {
			Node[] temp = new Node[0];
			return temp;
		}
		
		if (target.getSplitAxis(Axis.getAxis(depth)) > current.getSplitAxis(Axis.getAxis(depth))){
			if (current.hasRightChild()) {
				nodes.push(current);
				return KDSearch(target, range, current.getRightChild(), depth + 1, nodes);
			}
		}
		else if (current.hasLeftChild()) {
			nodes.push(current);
			return KDSearch(target, range, current.getLeftChild(), depth + 1, nodes);
		}
		
		Node[] bestNodes = new Node[range];
		Double[] bestDistances = new Double[range];
		Integer size = 0;
		
		if (target != current) {
			bestNodes[size] = current;
			bestDistances[size] = Math.pow(current.x - target.x, 2) + Math.pow(current.y - target.y, 2);
			size++;
		}
		
		Node n = current;
		
		while (!nodes.empty()){
			current = n;
			n = nodes.pop();
			depth--;
			Double currentDist = Math.pow(n.x - target.x, 2) + Math.pow(n.y - target.y, 2);
			
			if (n != target && (size < range || currentDist < bestDistances[size-1])){
				if (size < range) {
					bestDistances[size] = currentDist;
					bestNodes[size] = n;
					size++;
				}
				else {
					bestDistances[size-1] = currentDist;
					bestNodes[size-1] = n;
				}
				
				for (int i = size - 1; i > 0; i--) {
					if (bestDistances[i] < bestDistances[i-1]){
						Node temp = bestNodes[i];
						Double tempDist = bestDistances[i];
						bestNodes[i] = bestNodes[i-1];
						bestDistances[i] = bestDistances[i-1];
						bestNodes[i-1] = temp;
						bestDistances[i-1] = tempDist;
					}
				}
				
			}
			
			if (size < range || Math.pow(target.getSplitAxis(Axis.getAxis(depth)) - n.getSplitAxis(Axis.getAxis(depth)), 2) <= bestDistances[size-1]) {
				
				Node[] returnedNodes = new Node[0];
				if (n.getLeftChild() == current && n.hasRightChild()) returnedNodes = KDSearch(target, range, n.getRightChild(), depth + 1, new Stack<Node>());
				else if (n.hasLeftChild()) returnedNodes = KDSearch(target, range, n.getLeftChild(), depth + 1, new Stack<Node>());
				if (returnedNodes.length != 0) {
					Double[] nodeDistances = new Double[range];
					Node[] result = new Node[range];
					Double[] resultDistances = new Double[range];
					
					for (int i = 0; i < returnedNodes.length; i++) {
						nodeDistances[i] = Math.pow(returnedNodes[i].x - target.x, 2) + Math.pow(returnedNodes[i].y - target.y, 2);
					}
					
					int i = 0;
					int j = 0;
					int k = 0;
					while (i < returnedNodes.length && j < size && k < range){
						if (nodeDistances[i] != 0 && nodeDistances[i] < bestDistances[j]) {
							result[k] = returnedNodes[i];
							resultDistances[k] = nodeDistances[i];
							i++;
						}
						else {
							result[k] = bestNodes[j];
							resultDistances[k] = bestDistances[j];
							j++;
						}
						k++;
					}
					while (i < returnedNodes.length && k < range) {
						result[k] = returnedNodes[i];
						resultDistances[k] = nodeDistances[i];
						i++;
						k++;
					}
					while (j < size && k < range) {
						result[k] = bestNodes[j];
						resultDistances[k] = bestDistances[j];
						j++;
						k++;
					}
					size = k;
					bestNodes = result;
					bestDistances = resultDistances;
				}
			}
		}
		bestNodes = Arrays.copyOf(bestNodes, size);
		return bestNodes;
	}
	
	static Node[] KDRangeSearch(Node target, Double range, Node current, Integer depth, Stack<Node> nodes) {
		Double rangeSquared = Math.pow(range, 2);
		if (target.getSplitAxis(Axis.getAxis(depth)) > current.getSplitAxis(Axis.getAxis(depth))){
			if (current.hasRightChild()) {
				nodes.push(current);
				return KDRangeSearch(target, range, current.getRightChild(), depth + 1, nodes);
			}
		}
		else if (current.hasLeftChild()) {
			nodes.push(current);
			return KDRangeSearch(target, range, current.getLeftChild(), depth + 1, nodes);
		}
		
		List<Node> inRange = new ArrayList<Node>();
		List<Double> distances = new ArrayList<Double>();
		inRange.add(new Node("", 0.0, 0.0));
		distances.add(Double.NEGATIVE_INFINITY);
		

		Double tempDist = Math.pow(current.x - target.x, 2) + Math.pow(current.y - target.y, 2);
		if (current != target && tempDist < rangeSquared) {
			inRange.add(current);
			distances.add(tempDist);
		}
		
		Node n = current;
		
		while (!nodes.empty()){
			current = n;
			n = nodes.pop();
			depth--;
			Double currentDist = Math.pow(n.x - target.x, 2) + Math.pow(n.y - target.y, 2);
			if (n != target && currentDist <= rangeSquared) {
				for (int i = 0; i < inRange.size(); i++) {
					if (currentDist < distances.get(i)) {
						inRange.add(i, n);
						distances.add(i, currentDist);
						break;
					}
				}
				
				if (currentDist >= distances.get(distances.size()-1)){
					inRange.add(n);
					distances.add(currentDist);
				}
			}
			
			if (Math.pow(target.getSplitAxis(Axis.getAxis(depth)) - n.getSplitAxis(Axis.getAxis(depth)), 2) <= rangeSquared) {
				
				Node[] returnedNodes = new Node[0];
				if (n.getLeftChild() == current && n.hasRightChild()) returnedNodes = KDRangeSearch(target, range, n.getRightChild(), depth + 1, new Stack<Node>());
				else if (n.hasLeftChild()) returnedNodes = KDRangeSearch(target, range, n.getLeftChild(), depth + 1, new Stack<Node>());
				if (returnedNodes.length != 0) {
					Node[] inRangeArray = new Node[0];
					Double[] distanceArray = new Double[0];
					inRangeArray = inRange.toArray(inRangeArray);
					distanceArray = distances.toArray(distanceArray);
				   
					Double[] nodeDistances = new Double[inRangeArray.length + returnedNodes.length];
					Node[] result = new Node[inRangeArray.length + returnedNodes.length];
					Double[] resultDistances = new Double[inRangeArray.length + returnedNodes.length];
					
					for (int i = 0; i < returnedNodes.length; i++) {
						nodeDistances[i] = Math.pow(returnedNodes[i].x - target.x, 2) + Math.pow(returnedNodes[i].y - target.y, 2);
					}
					
					int i = 0;
					int j = 0;
					int k = 0;
					while (i < returnedNodes.length && j < inRangeArray.length){
						if (nodeDistances[i] < distanceArray[j]) {
							result[k] = returnedNodes[i];
							resultDistances[k] = nodeDistances[i];
							i++;
						}
						else {
							result[k] = inRangeArray[j];
							resultDistances[k] = distanceArray[j];
							j++;
						}
						k++;
					}
					while (i < returnedNodes.length) {
						result[k] = returnedNodes[i];
						resultDistances[k] = nodeDistances[i];
						i++;
						k++;
					}
					while (j < inRangeArray.length) {
						result[k] = inRangeArray[j];
						resultDistances[k] = distanceArray[j];
						j++;
						k++;
					}
					
					inRange = new ArrayList<Node>(Arrays.asList(result));
					distances = new ArrayList<Double>(Arrays.asList(resultDistances));
				}
			}
		}
		inRange.remove(0);
		Node[] toReturn = new Node[0];
		toReturn = inRange.toArray(toReturn);
		return toReturn;
	}
	
	static Node KDSingleNodeSearch(Node current, Node target, Integer depth, Stack<Node> nodes) {
		
		if (target.getSplitAxis(Axis.getAxis(depth)) > current.getSplitAxis(Axis.getAxis(depth))){
			if (current.hasRightChild()) {
				nodes.push(current);
				return KDSingleNodeSearch(current.getRightChild(), target, depth + 1, nodes);
			}
		}
		else if (current.hasLeftChild()) {
			nodes.push(current);
			return KDSingleNodeSearch(current.getLeftChild(), target, depth + 1, nodes);
		}
		
		Node bestNode;
		if (current == target) bestNode = nodes.get(0);
		else bestNode = current;
		
		Double bestDist = Math.pow(bestNode.x - target.x, 2) + Math.pow(bestNode.y - target.y, 2);
		
		Node n = current;
		
		while (!nodes.empty()){
			current = n;
			n = nodes.pop();
			depth--;
			Double currentDist = Math.pow(n.x - target.x, 2) + Math.pow(n.y - target.y, 2);
			if (currentDist < bestDist && currentDist != 0) {
				bestNode = n;
				bestDist = currentDist;
			}
			if (Math.pow(target.getSplitAxis(Axis.getAxis(depth)) - n.getSplitAxis(Axis.getAxis(depth)), 2) < bestDist) {
				if (n.getLeftChild() == current && n.hasRightChild()){
					Node a = KDSingleNodeSearch(n.getRightChild(), target, depth + 1, new Stack<Node>());
					Double aDist = Math.pow(a.x - target.x, 2) + Math.pow(a.y - target.y, 2);
					if (aDist < bestDist && aDist != 0) {
						bestNode = a;
						bestDist = aDist;
					}
				}
				else if (n.hasLeftChild()){
					Node a = KDSingleNodeSearch(n.getLeftChild(), target, depth + 1, new Stack<Node>());
					Double aDist = Math.pow(a.x - target.x, 2) + Math.pow(a.y - target.y, 2);
					if (aDist < bestDist && aDist != 0) {
						bestNode = a;
						bestDist = aDist;
					}
				}
			}
		}
		return bestNode;
	}
	
	static List<Node> NaiveSearch(List<Node> stars, Node target, Integer range) {
		List<Node> bestNodes = new ArrayList<Node>();
		List<Double> bestDistances = new ArrayList<Double>();
		bestNodes.add(new Node("", 0.0, 0.0));
		bestDistances.add(Double.POSITIVE_INFINITY);
		
		for (Integer i = 0; i < stars.size(); i++){
			Node a = stars.get(i);
			Double currentDist = Math.pow(a.x - target.x, 2) + Math.pow(a.y - target.y, 2);
			for (int j = 0; j < bestNodes.size(); j++) {
				if (currentDist < bestDistances.get(j) && currentDist != 0){
					bestNodes.add(j, a);
					bestDistances.add(j, currentDist);
					if (bestNodes.size() > range) {
						bestNodes.remove(bestNodes.size() - 1);
						bestDistances.remove(bestDistances.size() - 1);
					}
					break;
				}
			}
		}
		return bestNodes;
	}
	
	static List<Node> NaiveRangeSearch(List<Node> stars, Node target, Double range) {
		range = Math.pow(range, 2);
		List<Node> bestNodes = new ArrayList<Node>();
		List<Double> bestDistances = new ArrayList<Double>();
		bestNodes.add(new Node("", 0.0, 0.0));
		bestDistances.add(Double.NEGATIVE_INFINITY);
		
		
		for (Integer i = 0; i < stars.size(); i++){
			Node a = stars.get(i);
			Double currentDist = Math.pow(a.x - target.x, 2) + Math.pow(a.y - target.y, 2);
			
			if (currentDist < range) {
				if (currentDist < bestDistances.get(bestDistances.size()-1)){
					for (int j = 0; j < bestNodes.size(); j++) {
						if (currentDist < bestDistances.get(j)){
							bestNodes.add(j, a);
							bestDistances.add(j, currentDist);
							break;
						}
					}
				}
				
				else {
					bestNodes.add(a);
					bestDistances.add(currentDist);
				}
			}

		}
		bestNodes.remove(0);
		return bestNodes;
	}
}