package camel290.uri;

import java.util.Map;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.test.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class UriTest extends CamelTestSupport {

  /**
   * An URI of Camel Beanstalk component consists of a hostname, port and a list
   * of tube names. Tube names are separated by "+" character (which is more or less
   * usuall on the Web), but every tube name may contain URI special characters
   * like ? or +
   */

  class MyEndpoint extends DefaultEndpoint {
    String uri = null;
    String remaining = null;

    public MyEndpoint(final String uri, final String remaining) {
      this.uri = uri;
      this.remaining = remaining;
    }

    public Producer createProducer() throws Exception {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public Consumer createConsumer(Processor prcsr) throws Exception {
      throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSingleton() {
      return true;
    }
  }

  class MyComponent extends DefaultComponent {
    @Override
    protected Endpoint createEndpoint(final String uri, final String remaining, final Map<String, Object> parameters) throws Exception {
      return new MyEndpoint(uri, remaining);
    }
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();
    context.addComponent("my", new MyComponent());
  }

  @Test
  public void testExclamationInUri() {
    /**
     * %3F is not an ?, it's part of tube name.
     */
    MyEndpoint endpoint = context.getEndpoint("my:host:11303/tube1+tube%2B+tube%3F", MyEndpoint.class);
    assertNotNull("endpoint", endpoint);
  }

  @Test
  public void testPath() {
    /**
     * Here a tube name is "tube+" and written in URI as "tube%2B", but it gets
     * normalized, so that an endpoint sees "tube1+tube+"
     */
    MyEndpoint endpoint = context.getEndpoint("my:host:11303/tube1+tube%2B", MyEndpoint.class);
    assertEquals("Path contains several tube names, every tube name may have + or ? characters", "host:11303/tube1+tube%2B", endpoint.remaining);
  }
}
