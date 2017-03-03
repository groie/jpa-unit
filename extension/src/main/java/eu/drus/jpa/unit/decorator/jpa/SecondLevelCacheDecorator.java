package eu.drus.jpa.unit.decorator.jpa;

import javax.persistence.EntityManagerFactory;

import eu.drus.jpa.unit.core.metadata.FeatureResolver;
import eu.drus.jpa.unit.spi.TestMethodDecorator;
import eu.drus.jpa.unit.spi.TestMethodInvocation;

public class SecondLevelCacheDecorator implements TestMethodDecorator {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void apply(final TestMethodInvocation invocation) throws Throwable {
        final FeatureResolver resolver = invocation.getContext().createFeatureResolver(invocation.getMethod(),
                invocation.getTarget().getClass());

        final EntityManagerFactory emf = (EntityManagerFactory) invocation.getContext().getData("emf");

        evictCache(resolver.shouldEvictCacheBefore(), emf);

        try {
            invocation.proceed();
        } finally {
            evictCache(resolver.shouldEvictCacheAfter(), emf);
        }
    }

    private void evictCache(final boolean doEvict, final EntityManagerFactory emf) {
        if (doEvict) {
            emf.getCache().evictAll();
        }
    }

}