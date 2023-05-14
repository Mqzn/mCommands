package io.github.mqzn.commands.base.manager;

import io.github.mqzn.commands.arguments.*;

import java.util.HashMap;
import java.util.Map;

public final class ArgumentNumberComparator {
	
	private final Map<Class<? extends ArgumentNumber<?>>, ArgumentComparator<?>> comparators;
	
	public ArgumentNumberComparator() {
		comparators = new HashMap<>();
		
		registerComparator(ArgumentInteger.class, new IntegerComparator());
		registerComparator(ArgumentDouble.class, new DoubleComparator());
		registerComparator(ArgumentFloat.class, new FloatComparator());
		registerComparator(ArgumentLong.class, new LongComparator());
		
	}
	
	<N extends Number> void registerComparator(Class<? extends ArgumentNumber<N>> argClass, ArgumentComparator<N> comparator) {
		comparators.put(argClass, comparator);
	}
	
	@SuppressWarnings("unchecked")
	public <N extends Number> ArgumentComparator<N> comparatorOfArg(Class<? extends ArgumentNumber<N>> clazzArg) {
		return (ArgumentComparator<N>) comparators.get(clazzArg);
	}
	
	
	public interface ArgumentComparator<N extends Number> {
		
		boolean greaterThan(N n1, N n2);
		
		boolean greaterThanOrEqual(N n1, N n2);
		
		boolean lessThan(N n1, N n2);
		
		boolean lessThanOrEqual(N n1, N n2);
		
	}
	
	
	private static class IntegerComparator implements ArgumentComparator<Integer> {
		
		@Override
		public boolean greaterThan(Integer n1, Integer n2) {
			return n1 > n2;
		}
		
		@Override
		public boolean greaterThanOrEqual(Integer n1, Integer n2) {
			return n1 >= n2;
		}
		
		@Override
		public boolean lessThan(Integer n1, Integer n2) {
			return n1 < n2;
		}
		
		@Override
		public boolean lessThanOrEqual(Integer n1, Integer n2) {
			return n1 <= n2;
		}
	}
	
	private static class DoubleComparator implements ArgumentComparator<Double> {
		
		@Override
		public boolean greaterThan(Double n1, Double n2) {
			return n1 > n2;
		}
		
		@Override
		public boolean greaterThanOrEqual(Double n1, Double n2) {
			return n1 >= n2;
		}
		
		@Override
		public boolean lessThan(Double n1, Double n2) {
			return n1 < n2;
		}
		
		@Override
		public boolean lessThanOrEqual(Double n1, Double n2) {
			return n1 <= n2;
		}
	}
	
	private static class FloatComparator implements ArgumentComparator<Float> {
		
		@Override
		public boolean greaterThan(Float n1, Float n2) {
			return n1 > n2;
		}
		
		@Override
		public boolean greaterThanOrEqual(Float n1, Float n2) {
			return n1 >= n2;
		}
		
		@Override
		public boolean lessThan(Float n1, Float n2) {
			return n1 < n2;
		}
		
		@Override
		public boolean lessThanOrEqual(Float n1, Float n2) {
			return n1 <= n2;
		}
	}
	
	private static class LongComparator implements ArgumentComparator<Long> {
		
		@Override
		public boolean greaterThan(Long n1, Long n2) {
			return n1 > n2;
		}
		
		@Override
		public boolean greaterThanOrEqual(Long n1, Long n2) {
			return n1 >= n2;
		}
		
		@Override
		public boolean lessThan(Long n1, Long n2) {
			return n1 < n2;
		}
		
		@Override
		public boolean lessThanOrEqual(Long n1, Long n2) {
			return n1 <= n2;
		}
	}
	
}
