using System;
using Creator_Kit___RPG.Scripts.Gameplay;
using RPGM.Core;
using RPGM.Gameplay;

namespace Creator_Kit___RPG.Scripts.Core
{
    /// <summary>
    /// An event allows execution of some logic to be deferred for a period of time.
    /// </summary>
    /// <typeparam name="Event"></typeparam>
    public abstract class Event : IComparable<Event>
    {
        public virtual void Execute() { }

        protected GameModel model = Schedule.GetModel<GameModel>();

        internal float tick;

        public int CompareTo(Event other)
        {
            return tick.CompareTo(other.tick);
        }

        internal virtual void ExecuteEvent() => Execute();

        internal virtual void Cleanup()
        {

        }
    }

    /// <summary>
    /// Add functionality to the Event class to allow the observer / subscriber pattern.
    /// </summary>
    /// <typeparam name="T"></typeparam>
    public abstract class Event<T> : Event where T : Event<T>
    {
        public static Action<T> OnExecute;

        internal override void ExecuteEvent()
        {
            Execute();
            OnExecute?.Invoke((T)this);
        }
    }

}