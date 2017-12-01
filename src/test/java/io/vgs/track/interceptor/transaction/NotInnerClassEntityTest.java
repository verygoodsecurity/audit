package io.vgs.track.interceptor.transaction;

import com.google.common.collect.Lists;

import org.junit.Test;

import io.vgs.track.BaseTest;

public class NotInnerClassEntityTest extends BaseTest {

  @Test
  public void failFastTest() {
    Long entityId = doInJpa(em -> {
      SimpleEnity entity = new SimpleEnity("test", Lists.newArrayList("bmw", "audi"));
      em.persist(entity);
      return entity.getId();
    });

    doInJpa(em -> {
      SimpleEnity simpleEnity = em.find(SimpleEnity.class, entityId);
      simpleEnity.getCars().add("Lamborghini");
    });

    System.out.println(testEntityTrackingListener.getInserts());
    System.out.println(testEntityTrackingListener.getUpdates());
  }

  @Override
  protected Class<?>[] entities() {
    return new Class<?>[]{
        SimpleEnity.class
    };
  }
}
