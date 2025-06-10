package redactedrice.universalrandomizer.userobjectapis;

@FunctionalInterface
public interface Sum<T> 
{
	public T sum(T lhs, T rhs);
}
