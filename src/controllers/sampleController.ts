//sample controller 입니다.
import { Request, Response, Router } from 'express';

const router: Router = Router();

router.get('/', (req: Request, res: Response) => {
  const str = 'Sample Get Router';
  res.send(str);
});

router.post('/', (req: Request, res: Response) => {
  const str = 'Sample Post Router';
  res.send(str);
});

export const sampleController: Router = router;
