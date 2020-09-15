package onl.tesseract.tesseractlib.player;

import onl.tesseract.tesseractlib.TesseractLib;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Group {
    public static List<Group> groupList = new ArrayList<>();
    List<TPlayer> members;
    List<TPlayer> invited;
    TPlayer leader;
    List<TPlayer> admin;
    UUID uuid;
    Scoreboard scoreboard;
    Objective displayLife;
    BukkitTask updateScoreboard;

    public Group(Player p){
        members = new ArrayList<>();
        invited = new ArrayList<>();
        admin = new ArrayList<>();
        TPlayer tp = TPlayer.get(p);
        members.add(tp);
        admin.add(tp);
        this.leader = tp ;
        this.uuid = UUID.randomUUID();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.displayLife = this.scoreboard.registerNewObjective("Vies","","Groupe");
        this.displayLife.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.displayLife.setDisplayName("Groupe");
        groupList.add(this);
        this.updateScoreboard = new BukkitRunnable(){
            @Override
            public void run()
            {
                updateScoreboard();
            }}.runTaskTimer(TesseractLib.instance,0,10);

    }

    public List<TPlayer> getMembers() {
        return members;
    }


    public void addMember(TPlayer p){
        if (isInvited(p))
        {
            invited.remove(p);
        }
        if(members.isEmpty())
        {
            setLeader(p);
            addAdmin(p);
        }
        members.add(p);
        p.setGroup(this);

    }

    public void setLeader(TPlayer p){
        if(!admin.contains(p))admin.add(p);
        leader = p;
    }

    public void addAdmin(TPlayer p){
        admin.add(p);
    }
    public void removeAdmin(TPlayer p){
        admin.remove(p);
    }
    public void removeMember(TPlayer p){

        if(isGrouped(p)){
            members.remove(p);
            admin.remove(p);
            p.setGroup(null);
            p.getBukkitPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
        else if(isInvited(p)) {
            invited.remove(p);
        }
        if(isLeader(p)){
            if(members.isEmpty())
                return;
            if(admin.isEmpty()){
                setLeader(members.get(0));
                return;
            }
            setLeader(admin.get(0));
        }
    }

    public void addInvited(TPlayer p){
        invited.add(p);
    }

    public boolean isGrouped(TPlayer p){
        return members.contains(p);
    }

    static public boolean areGrouped(Player a, Player b)
    {
        return TPlayer.get(a).hasGroup() && TPlayer.get(a).getGroup().isGrouped(TPlayer.get(b));
    }

    public boolean isInvited(TPlayer p){
        return invited.contains(p);
    }
    public boolean isAdmin(TPlayer tPlayer){
        return admin.contains(tPlayer) || leader.equals(tPlayer);
    }
    public boolean isFull(){
        return members.size()  >= 15;
    }
    public boolean isLeader(TPlayer tPlayer){
        return leader.equals(tPlayer);
    }
    private ChatColor getColor(Player player){
        float hp = (float)player.getHealth();
        if(player.isDead()){
            return ChatColor.GRAY;
        }
        else if(hp >= 20){
            return ChatColor.GREEN;
        }
        else if(hp >= 16){
            return ChatColor.DARK_GREEN;
        }
        else if(hp >= 12){
            return ChatColor.YELLOW ;
        }
        else if(hp >= 8){
            return ChatColor.GOLD ;
        }
        else if(hp >= 4){
            return ChatColor.RED ;
        }
        else {
            return ChatColor.DARK_RED ;
        }
    }
    public void updateScoreboard(){
        this.displayLife.unregister();
        this.displayLife = this.scoreboard.registerNewObjective("Vies","","Groupe");
        this.displayLife.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.displayLife.setDisplayName(ChatColor.BLUE +""+ChatColor.BOLD+"Groupe");
        int nbmembers = members.size();
        int i = nbmembers-1;
        //Update leader
        Player leader = this.leader.getBukkitPlayer();
        this.displayLife.getScore(ChatColor.DARK_PURPLE + "* " + getColor(leader)+ leader.getName()).setScore(i--);
        //Update admin
        for(TPlayer tp : admin){
            if(this.leader.equals(tp))
                continue;
            Player tmp = tp.getBukkitPlayer();
            this.displayLife.getScore(ChatColor.LIGHT_PURPLE + "* " + getColor(tmp)+ tmp.getName()).setScore(i--);
        }
        for(TPlayer tp : members){
            if(this.leader.equals(tp)){
                continue;
            }
            if(this.admin.contains(tp)){
                continue;
            }
            Player tmp = tp.getBukkitPlayer();
            this.displayLife.getScore( getColor(tmp)+ tmp.getName()).setScore(i--);
        }
        for (TPlayer tp : members){
            tp.getBukkitPlayer().setScoreboard(this.scoreboard);
        }

    }

    public String getColoredMember(TPlayer player) {
        ChatColor color;
        if (isLeader(player))
            color = ChatColor.RED;
        else if (isAdmin(player))
            color = ChatColor.GOLD;
        else
            color = ChatColor.GREEN;
        return color + player.getOfflinePlayer().getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return uuid.equals(group.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
