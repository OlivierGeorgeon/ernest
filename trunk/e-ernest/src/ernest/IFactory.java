package ernest;

import java.util.List;

/**
 * Factory classes are solely responsible for creating the objects that 
 * determine how ernest behaves.  This makes then entire ernest codebase
 * completely ignorant of the specific types of objects it uses.  
 * By creating a custom factory, the modeler can tweak various aspects 
 * of ernest's behavior by substituting different implementations of the objects 
 * used by ernest.
 * @author mcohen
 *
 */
public interface IFactory 
{
	public IEnvironment getEnvironment();
	
	public IAlgorithm getAlgorithm();
	
	public IAct createAct(ISchema s, boolean success, int satisfaction);
	
	public ISchema addSchema(List<ISchema> l, IAct context, IAct intention);
	
	public ISchema createSchema(int id);
	
	public ISchema createPrimitiveSchema(int id, String tag, int successSat, int failureSat);
	
	public IProposition createProposition(ISchema schema, int w, int e);
	
	public IContext createContext();
}
