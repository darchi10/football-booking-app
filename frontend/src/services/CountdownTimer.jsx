import { useEffect, useState } from 'react';
import { parseISO, differenceInSeconds } from 'date-fns';


const CountdownTimer = ({ startTime }) => {
  const [timeLeft, setTimeLeft] = useState('');
  const [secondsLeft, setSecondsLeft] = useState(null);

  useEffect(() => {
    const updateCountdown = () => {
      const now = new Date();
      const start = parseISO(startTime);
      const totalSeconds = Math.max(0, differenceInSeconds(start, now));
      setSecondsLeft(totalSeconds);

      const days = Math.floor(totalSeconds / 86400);
      const hours = Math.floor((totalSeconds % 86400) / 3600);
      const minutes = Math.floor((totalSeconds % 3600) / 60);
      const seconds = totalSeconds % 60;

      const parts = [];
      if (days > 0) parts.push(`${days} dana`);
      if (hours > 0 || days > 0) parts.push(`${hours} sati`);
      if (minutes > 0 || hours > 0 || days > 0) parts.push(`${minutes} minuta`);
      parts.push(`${seconds} sekundi`);

      setTimeLeft(parts.join(', '));
    }

    updateCountdown();
    const interval = setInterval(updateCountdown, 1000);

    return () => clearInterval(interval);
  }, [startTime]);

  if (secondsLeft === 0) {
    return <p className='text-gray-700'>Rezervacija završila.</p>;
  }

  return (
    <p className='text-gray-700'>
      Vrijeme preostalo do rezervacije: <span className='font-bold'>{timeLeft}</span>
    </p>
  );
};

export default CountdownTimer;
