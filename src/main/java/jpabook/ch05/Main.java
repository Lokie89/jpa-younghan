package jpabook.ch05;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Main {

    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
    private static EntityManager em = emf.createEntityManager();
    private static EntityTransaction tx = em.getTransaction();

    public static void main(String[] args) {
        try {
            tx.begin();
            testSave();
//            queryLogicJoin();
//            updateRelation();
            biDirection();
//            deleteRelation();
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    public static void testSave() {
        Team team1 = new Team("team1", "팀1");
        em.persist(team1);

        Member member1 = new Member("member1", "회원1");
        member1.setTeam(team1);
        em.persist(member1);

        Member member2 = new Member("member2", "회원2");
        member2.setTeam(team1);
        em.persist(member2);

        Member member3 = em.find(Member.class, "member1");
        Team team = member3.getTeam();
        System.out.println("팀 이름 = " + team.getName());

    }


    private static void queryLogicJoin() {
        String jpql = "select m from Member m join m.team t where t.name =:teamName";

        List<Member> resultList = em.createQuery(jpql, Member.class)
                .setParameter("teamName", "팀1")
                .getResultList();

        for (Member member : resultList) {
            System.out.println("[query] member.username = " + member.getUsername());
        }
    }

    private static void updateRelation() {
        Team team2 = new Team("team2", "팀2");
        em.persist(team2);
        Member member = em.find(Member.class, "member1");
        member.setTeam(team2);
    }

    private static void deleteRelation() {
        Member member1 = em.find(Member.class, "member1");
        member1.setTeam(null);
    }

    private static void biDirection() {
        Team team = em.find(Team.class, "team1");
        List<Member> members = team.getMembers();
        System.out.println("###############################"+members.size());
        for (Member member : members) {
            System.out.println("member.username = " + member.getUsername());
        }
    }
}
