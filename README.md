# Team GitHub Workflow  
This is a short guide to setting up GitHub, summarised with ChatGPT from the [Software Tools Github lab](https://github.com/cs-uob/software-tools/blob/main/05-git/lab/README.md). Please read it before you start working.
  
## Branches  
- `main` = stable/final version  
- `develop` = shared working branch  
- personal branches = each person’s own work branch  
  
Do **not** work directly on `main`.  
  
## First-time setup  
Clone the repo and switch to `develop`:  
  
```bash  
git clone git@github.com:RealHarrisonHui/Scotland-Yard.git  
cd Scotland-Yard  
git fetch  
git checkout develop  
git pull
```
## Starting new work

Before you start, make sure `develop` is up to date, then create or switch to your own branch:
```bash
git checkout develop  
git pull  
git checkout -b your-branch-name
```

## While working

Commit your changes regularly:
```bash
git add .  
git commit -m  "Describe your changes"
```

Push your branch to GitHub:
```bash
git push --set-upstream origin your-branch-name
```
After the first push, just use:
```bash
git push
```
## When your work is ready

Push your latest changes, then open a Pull Request on GitHub:

-   Base branch: `develop`
    
-   Compare branch: `your-branch-name`
    

After review, merge the Pull Request into `develop`.

## Getting the latest shared files

To bring the newest files from `develop` into your branch:
```bash
git checkout develop  
git pull  
git checkout your-branch-name  
git merge develop
```
Then push again:
```bash
git push
```

## Rules

-   Never work directly on `main`
    
-   Keep `develop` updated
    
-   Each person works on their own branch
    
-   Open Pull Requests into `develop`
    
-   Only merge to `main` when the project is stable/final
    
-   Do not use `push --force` on shared branches
