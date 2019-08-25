package dev.latvian.kubejs.server;

import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.KubeJSEvents;
import dev.latvian.kubejs.event.EventsJS;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = KubeJS.MOD_ID)
public class KubeJSServerEventHandler
{
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onServerTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
		{
			return;
		}

		if (!ServerJS.instance.scheduledEvents.isEmpty())
		{
			long now = System.currentTimeMillis();
			Iterator<ScheduledEvent> eventIterator = ServerJS.instance.scheduledEvents.iterator();
			List<ScheduledEvent> list = new ArrayList<>();

			while (eventIterator.hasNext())
			{
				ScheduledEvent e = eventIterator.next();

				if (now >= e.endTime)
				{
					list.add(e);
					eventIterator.remove();
				}
			}

			for (ScheduledEvent e : list)
			{
				try
				{
					e.call();
				}
				catch (Exception ex)
				{
					KubeJS.LOGGER.error("Error occurred while handling scheduled event callback in " + e.file.path + ": " + ex);
					ex.printStackTrace();
				}
			}
		}

		EventsJS.INSTANCE.post(KubeJSEvents.SERVER_TICK, new ServerEventJS(ServerJS.instance));
	}
}